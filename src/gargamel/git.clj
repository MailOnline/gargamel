(ns gargamel.git
  (:require [gargamel.util :as util]
            [clojure.java.shell :as sh]))

(defn checkout-project [mod-name repo]
  (let [tmp-dir (.getAbsolutePath (util/mk-tmp-dir (name mod-name)))
        res (sh/sh "git" "clone" repo tmp-dir)]
    tmp-dir))

(defn remote-url [source-dir]
  (sh/sh "git" "config" "--get" "remote.origin.url" :dir source-dir))
