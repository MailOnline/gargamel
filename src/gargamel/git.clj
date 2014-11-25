(ns gargamel.git
  (:require [gargamel.util :as util]
            [clojure.java.shell :as sh]))

(defn real-project-name [pn]
  (if (= pn "electrostatic") "f_electrostatic" pn))

(defn checkout-project [mod-name repo]
  (let [tmpdir (util/mk-tmp-dir (name mod-name))
        tmpdir-name (.getAbsolutePath tmpdir)]
    (when-not (.exists tmpdir) (.isDirectory tmpdir)
      (throw (Exception. "Can't run git on non-existent dir " tmpdir-name)))
    (sh/sh "git" "clone" repo tmpdir-name)
    tmpdir))

(defn remote-url* [source-dir]
  (:out (sh/sh "git" "config" "--get" "remote.origin.url" :dir source-dir)))

(def remote-url (memoize remote-url*))

(defn commit-url [source-dir project-name hash]
  (let [project-name (real-project-name project-name)]
    (if (re-find #"bitbucket" (remote-url source-dir) )
      (format "https://bitbucket.org/MailOnline/%s/commits/%s" project-name hash)
      (format "https://github.com/MailOnline/%s/commit/%s" project-name hash))))

(defn compare-url [source-dir project-name from to]
  (let [project-name (real-project-name project-name)]
    (if (re-find #"bitbucket" (remote-url source-dir))
      (format "https://bitbucket.org/MailOnline/%s/branches/compare/%s..%s" project-name from to)
      (format "http://github.com/MailOnline/%s/compare/%s...%s" project-name from to))))
