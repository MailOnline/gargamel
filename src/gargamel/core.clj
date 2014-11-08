(ns gargamel.core
  (:gen-class)
  (:require [leiningen.gargamel :as grg]
            [leiningen.gargamel-lr :as grg-lr]
            [clojure.tools.cli :as cli]
            [clojure.string :as str]))

(def gargamel-quotes
  ["Son of a smurf!"
   "I see no transformation. Your potion has no power. She's still an eye-offending dogfish if you ask me."
   "Somebody's been working a dark and terrible magic in there."
   "Oh, Azrael, we've come so far, yet I am haunted by the same familiar problem: how to find the Smurfs."
   "Must... have... SMURFS!"
   "Ooo, those goody-goody Smurfs make me sick!"
   "Azrael you Miserable Cat!"])

(def cli-options
  [
   ["-f" "--from FROM" "From ref (either commit hash or tag)"]
   ["-t" "--to TO" "To ref (either commit hash or tag). If empty defaults to HEAD."
    :default "HEAD"]
   ["-r" "--latest-release" "Generates release notes for the latest release: between the two latest release tags. Tag format expected to be release-buildnumber-date_time: release-\\d+-\\d+_\\d+"]
   ["-p" "--project-name NAME" "Name of the project"]
   ["-d" "--target-dir DIR" "Directory to generate create the changelog file in"]
   ["-h" "--help"]
   ["-v" "--verbose"]])

(defn -main [& args]
  (let [opts (cli/parse-opts args cli-options)
        {:keys [from to latest-release project-name target-dir help verbose]} (:options opts)
        from-or-lr (or from latest-release)]

    (when help
      (println (:summary opts)))

    (when verbose
      (println (str "   from: " from))
      (println (str "   to: " to))
      (println (str "   latest-release: " latest-release))
      (println (str "   project-name: " project-name))
      (println (str "   target-dir: " target-dir)))

    (when (and from latest-release)
      (println "Either provide from and optionally to OR latest-release. Make up your mind!")
      (System/exit 1))

    (when (and (not help) (not from-or-lr))
      (println "Please provide FROM or run in latest-release mode")
      (System/exit 1))

    (when from
      (grg/gargamel-changelog project-name target-dir from to))

    (when latest-release
      (grg-lr/gargamel-latest-release-notes project-name target-dir)))
  (println (first (shuffle gargamel-quotes)))
  (System/exit 0))
