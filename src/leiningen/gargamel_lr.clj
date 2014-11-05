(ns leiningen.gargamel-lr
  (:require [clojure.java.shell :as sh]
            [leiningen.gargamel :refer [gargamel]]
            [clojure.string :as str]))

(defn- latest-release-from-to []
  (->> (-> (sh/sh "git" "log" "--date=short" "--format=%h%;%cN;%d;%ad;%s;%b;" "--no-merges" :dir ".")
           :out
           (str/split #";"))
       (partition 6)
       (map #(nth % 2))
       (filter #(re-matches #".*release-.*" %))
       (map #(str/replace % #".*(release-\d+-\d+_\d+).*" "$1"))
       (take 2)
       (reverse)))

(defn gargamel-lr
  "Generates html changelog file between latest release tags.

   Release tags format expected to be \"release-buildnumber-date_time\""
  [project]
  (apply (partial gargamel project) (latest-release-from-to)))
