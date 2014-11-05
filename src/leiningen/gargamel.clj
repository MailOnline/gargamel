(ns leiningen.gargamel
  (:require [clojure.java.shell :as sh]
            [clojure.string :as str]
            [leiningen.core.main :as lm]
            [stencil.core :as st]))

(def ^:dynamic proj-name nil)

(defn- github-issue-link [proj-name]
  (format "<a href=\"https://github.com/MailOnline/%s/issues/$1\"> #$1</a>" proj-name))

(def github-issue-link-external "<a href=\"https://github.com/$2/issues/$3\">$2: $3</a>")

(def github-issue-regexp-string "#(\\d+)")

(def github-issue-regexp (re-pattern github-issue-regexp-string))

(def github-external-issue-regexp #"(([_\w/-]+)#(\d+))")

(def jira-issue-link "<a href=\"http://andjira.and.dmgt.net:8080/browse/$1\">$1</a>")

(def jira-issue-regex-string "(MOL-\\d+)")

(def jira-issue-regex (re-pattern jira-issue-regex-string))

(defn changelog [from to]
  {:pre [from]}
  (->> (-> (sh/sh "git" "log" "--date=short" "--format=%h%;%cN;%d;%ad;%s;%b;" "--no-merges" (format "%s..%s" from to) :dir ".")
           :out
           (str/split #";"))
       (partition 6)
       (map #(zipmap [:hash :commiter :refs :date :subject :body] %))
       (map #(assoc % :hash (apply str (butlast (-> % :hash)))))))

(defn latest-release-from-to []
  (->> (-> (sh/sh "git" "log" "--date=short" "--format=%h%;%cN;%d;%ad;%s;%b;" "--no-merges" :dir ".")
           :out
           (str/split #";"))
       (partition 6)
       (map #(nth % 2))
       (filter #(re-matches #".*release-.*" %))
       (map #(str/replace % #".*(release-\d+-\d+_\d+).*" "$1"))
       (take 2)
       (reverse)))

(defn render-html-changelog [from to changes]
  (let [[to-release to-build-num to-time] (str/split to #"-")
        [from-release from-build-num from-time] (str/split from #"-")
        from-params (if (and from-build-num from-time)
                      (merge {:from from}
                             {:from-release from-release
                              :from-build-num from-build-num
                              :from-time (str/replace from-time #"_" " time: ")})
                      {:from from})
        to-params (if (and to-build-num to-time)
                    (merge {:to to}
                           {:to-release to-release
                            :to-build-num to-build-num
                            :to-time (str/replace to-time #"_" " time: ")})
                      {:to to})]
    (st/render-file "changelog" (merge {:commits changes
                                        :global {:project-name proj-name}}
                                       from-params to-params))))

(defn issues->links [commit]
  (let [i->l (fn [t] (-> t
                         (str/replace github-external-issue-regexp github-issue-link-external)
                         (str/replace github-issue-regexp (github-issue-link proj-name))
                         (str/replace jira-issue-regex jira-issue-link)))
        subject (-> commit
                    :subject
                    i->l)
        body (-> commit :body i->l)]
    (assoc commit :linked-body (str/replace body #"\n" "<br/>") :linked-subject subject)))

(defn create-section [commit]
  (let [subject (:subject commit)
        body (:body commit)
        jira-pattern (re-pattern (format ".*%s.*" jira-issue-regex-string))
        github-pattern (re-pattern (format ".*%s.*" github-issue-regexp-string))
        refactor-pattern #"refactor"]
    (cond (or (re-matches jira-pattern subject)
              (re-matches jira-pattern body))
          :business

          (or (re-matches github-pattern subject)
              (re-matches github-pattern body))
          :technical

          (or (re-matches refactor-pattern subject)
              (re-matches refactor-pattern body))
          :refactor

          :default
          :other)))

(defn section-flags [changes]
  (-> changes
      (#(if (:business %) (assoc % :business-flag true) %))
      (#(if (:technical %) (assoc % :technical-flag true) %))
      (#(if (:refactor %) (assoc % :refactor-flag true) %))))

(defn create-html-changelog [from to]
  (let [to (or to "HEAD")
        changelog (->> to
                       (changelog from)
                       (map issues->links)
                       (group-by create-section)
                       section-flags)]
    (spit (format "/tmp/changelog-%s-%s.html" from to) (render-html-changelog from to changelog))))

(defn gargamel
  "Generates html changelog file between to commits or tags

  You have to provide the first parameter (from ref) second parameter is
  optional. In case it is not provided changelog will be generated between
  from and HEAD."
  [project from & to]
  (binding [proj-name (:name project)]
    (lm/info (format "Generating changelog for project %s between %s and %s" proj-name from (first to)))
    (create-html-changelog from (first to))))
