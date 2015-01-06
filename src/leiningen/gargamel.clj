(ns leiningen.gargamel
  (:require [clojure.string :as str]
            [gargamel.git :as git]
            [stencil.core :as st]
            [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import java.io.File))

(def ^:dynamic proj-name nil)

(def ^:dynamic target-path nil)

(defn- github-issue-link [org-name proj-name]
  (format "<a href=\"https://github.com/%s/%s/issues/$1\"> #$1</a>" org-name proj-name))

(def ^:private github-issue-link-external "<a href=\"https://github.com/$2/issues/$3\">$2: $3</a>")

(def ^:private github-issue-regexp-string "#(\\d+)")

(def ^:private github-issue-regexp (re-pattern github-issue-regexp-string))

(def ^:private github-external-issue-regexp #"(([_\w/-]+)#(\d+))")

(def ^:private jira-issue-link "<a href=\"http://andjira.and.dmgt.net:8080/browse/$1\">$1</a>")

(def ^:private jira-issue-regex-string "(MOL-\\d+)")

(def ^:private jira-issue-regex (re-pattern jira-issue-regex-string))

(def ^:private sections-defaults
  [{:key :refactor :regex ".*refactor.*" :title "Refactorings, improvements"}
   {:key :technical :regex ".*#(\\d+).*" :title "Internal changes"}
   {:key :business :regex ".*(MOL-\\d+).*" :title  "Business related changes"}])

(def project-config
  (let [proj-config-name "gargamel.edn"]
    (when (.exists (io/file proj-config-name))
      (edn/read-string (slurp proj-config-name)))))

(defn changelog [from to project]
  {:pre [from]}
  (let [raw-changelog (git/changelog from to (:dir project))]
    (->> (str/split raw-changelog #";")
         (partition 6)
         (map #(zipmap [:hash :commiter :refs :date :subject :body] %))
         (map #(assoc % :hash (apply str (butlast (-> % :hash)))))
         (map #(update-in % [:hash] str/replace #"\W" ""))
         (map #(assoc % :project-name (:name project)))
         (map #(assoc % :url (git/commit-url (:dir project) (:name project) (:hash %)))))))

(defn render-html-changelog [from to changes source-dir]
  (let [[to-release to-build-num to-time] (str/split to #"-")
        [from-release from-build-num from-time] (str/split from #"-")
        from-params (if (and from-build-num from-time)
                      (merge {:from from}
                             {:from-release from-release
                              :from-build-num from-build-num
                              :from-time (str/replace from-time #"_" " time: ") })
                      {:from from })
        to-params (if (and to-build-num to-time)
                    (merge {:to to}
                           {:to-release to-release
                            :to-build-num to-build-num
                            :to-time (str/replace to-time #"_" " time: ")})
                    {:to to})]
    (st/render-file "changelog" (merge {:sections (vals changes)
                                        :global {:project-name proj-name}
                                        :from-url (git/commit-url source-dir proj-name from)
                                        :to-url (git/commit-url source-dir proj-name to)
                                        :compare-url (git/compare-url source-dir proj-name from to)}
                                       from-params to-params))))

(defn issues->links [source-dir commit]
  (let [org-name (git/org-or-username (git/remote-url source-dir))
        i->l (fn [t] (-> t
                         (str/replace github-external-issue-regexp github-issue-link-external)
                         (str/replace github-issue-regexp (github-issue-link org-name proj-name))
                         (str/replace jira-issue-regex jira-issue-link)))
        subject (-> commit
                    :subject
                    i->l)
        body (-> commit :body i->l)]
    (assoc commit :linked-body (str/replace body #"\n" "<br/>") :linked-subject subject)))

(defn- create-section [sections-config commit]
  (let [subject (:subject commit)
        body (:body commit)]
    (or (->> sections-config
             reverse
             (some #(when (or (re-matches (-> % :regex re-pattern) subject)
                          (re-matches (-> % :regex re-pattern) body)) (:key %))))
        :other)))

(defn- section-titles [sections-config changes]
  (let [titles (zipmap (vec (cons :other (map :key sections-config))) (vec (cons "Other changes" (map :title sections-config))))]
    (->> changes
         (map (fn [[k commits]] [k {:title (get titles k) :commits commits}]))
         identity
         (sort-by #(.indexOf (keys titles) (first %)))
         (into (array-map)))))

(defn enrich-changelog [log source-dir]
  (let [sections-config (or (:sections project-config) sections-defaults)]
    (->> log
         (map (partial issues->links source-dir))
         (group-by (partial create-section sections-config))
         ((partial section-titles sections-config)))))

(defn create-html-changelog [changes from to source-dir]
  (let [to (or to "HEAD")
        target-dir (File. target-path)]
    (when-not (.exists target-dir)
      (.mkdirs target-dir))
    (spit (format "%s/changelog-%s-%s.html" target-path from to)
          (render-html-changelog from to changes source-dir))))

(defn gargamel-changelog [project-name path project-dir from to]
  (binding [proj-name project-name
            target-path path]
    (let [proj-dir (or project-dir ".")]
      (println (format "Generating changelog for project %s between %s and %s" project-name from to))
      (create-html-changelog (enrich-changelog (changelog from to {:name project-name :dir proj-dir}) proj-dir)
                             from to proj-dir))))

(defn gargamel
  "Generates html changelog file between to commits or tags

  You have to provide the first parameter (from ref) second parameter is
  optional. In case it is not provided changelog will be generated between
  from and HEAD."
  [project from & to]
  (let [proj-name (:name project)
        target-path (:target-path project)]
    (gargamel-changelog proj-name target-path nil from (first to))))
