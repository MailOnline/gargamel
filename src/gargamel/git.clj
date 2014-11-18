(ns gargamel.git
  (:require [gargamel.util :as util]
            [clojure.java.shell :as sh]))

(defn checkout-project [mod-name repo]
  (let [tmpdir (util/mk-tmp-dir (name mod-name))
        tmpdir-name (.getAbsolutePath tmpdir)]
    (when-not (.exists tmpdir) (.isDirectory tmpdir)
      (throw (Exception. "Can't run git on non-existent dir " tmpdir-name)))
    (sh/sh "git" "clone" repo tmpdir-name)
    tmpdir))

(defn remote-url [source-dir]
  (sh/sh "git" "config" "--get" "remote.origin.url" :dir source-dir))
