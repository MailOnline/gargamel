(ns leiningen.gargamel-lr
  (:require [clojure.java.shell :as sh]
            [leiningen.gargamel :as gr]
            [clojure.string :as str]))

(defn- latest-release-from-to []
  (->> (-> (sh/sh "git" "log" "--date=short" "--format=%h%;%cN;%d;%ad;%s;%b;" :dir ".")
           :out
           (str/split #";"))
       (partition 6)
       (map #(nth % 2))
       (filter #(re-matches #".*release-.*" %))
       (map #(str/replace % #".*(release-\d+-\d+_\d+).*" "$1"))
       (take 2)
       (reverse)))

(defn gargamel-latest-release-notes [proj-name target-dir]
  (apply (partial gr/gargamel-changelog proj-name target-dir) (latest-release-from-to)))

(defn gargamel-lr
  "Generates html changelog file between latest release tags.

   Release tags format expected to be \"release-buildnumber-date_time\""
  [project]
  (apply (partial gr/gargamel project) (latest-release-from-to)))
