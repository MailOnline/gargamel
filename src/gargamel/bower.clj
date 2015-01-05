(ns gargamel.bower
  (:require [cheshire.core :as json]
            [clojure
             [data :as data]
             [string :as s]]
            [clojure.java.shell :as sh]
            [gargamel.git :as git]
            [leiningen.gargamel :as grg]))

(defn- config [project-dir & {:keys [rev] :or {rev "HEAD"}}]
  (json/parse-string (:out (sh/sh "git" "show" (str rev ":bower.json" )
                                  :dir project-dir)) true))

(defn lookup-module [project-dir mod-name]
  (apply str (-> (sh/sh "bower" "lookup" (name mod-name) :dir project-dir)
                 :out
                 (s/split #" ")
                 second
                 butlast)))

(defn- merge-versions [x y]
  (map #(s/replace % #"[^\d\.]" "") [x y]))


(defn changelog
  [project-name project-dir from to]
  (let [deps-before (:dependencies (config project-dir :rev from))
        deps-after (:dependencies (config project-dir :rev to))
        deps-changed (data/diff deps-before deps-after)
        mods-changed (merge-with merge-versions (first deps-changed) (second deps-changed))
        changes (grg/changelog from to {:dir project-dir :name (name project-name)
                                        :remote-url (git/remote-url project-dir)})]
    (if mods-changed
      (concat changes
              (flatten
                (doall
                  (for [[mod-name [mod-old-version mod-new-version]] mods-changed
                        :let [repo (lookup-module project-dir mod-name)
                              mod-dir (git/checkout-project mod-name repo)]]
                    (binding [grg/proj-name mod-name]
                      (changelog mod-name mod-dir mod-old-version mod-new-version))))))
      changes)))

(defn bower-changelog [source-path target-path from to]
  (binding [grg/proj-name (:name (config source-path))
            grg/target-path target-path]
    (println (format "Generating changelog for project %s between %s and %s"
                     grg/proj-name
                     from to))
    (grg/create-html-changelog (grg/enrich-changelog
                                 (changelog grg/proj-name source-path from to))
                               from to source-path)))
