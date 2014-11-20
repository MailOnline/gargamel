(ns leiningen.gargamel-lr
  (:require [clojure.java.shell :as sh]
            [leiningen.gargamel :as gr]
            [clojure.string :as str]))

(def ^:private default-release-tag-pattern
  "release-\\d+-\\d+_\\d+")

(defn- latest-release-from-to [rt-pattern]
  (->> (-> (sh/sh "git" "log" "--date=short" "--format=%h%;%cN;%d;%ad;%s;%b;" :dir ".")
           :out
           (str/split #";"))
       (partition 6)
       (map #(nth % 2))
       (filter #(re-matches (re-pattern (str ".*" rt-pattern ".*")) %))
       (map #(str/replace % (re-pattern (str ".*(" rt-pattern ").*")) "$1"))
       (take 2)
       (reverse)))

(defn gargamel-latest-release-notes [proj-name target-dir rt-pattern]
  (let [rt-pattern (or rt-pattern default-release-tag-pattern)]
    (apply (partial gr/gargamel-changelog proj-name target-dir) (latest-release-from-to rt-pattern))))

(defn gargamel-lr
  "Generates html changelog file between latest release tags.

   Release tags format expected to be \"release-buildnumber-date_time\""
  [project & release-tag-pattern]
  (let [rt-pattern (or (first release-tag-pattern) default-release-tag-pattern)]
    (apply (partial gr/gargamel project) (latest-release-from-to rt-pattern))))
