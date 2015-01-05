(ns gargamel.util
  (:require [clojure.java.io :as io]))

(defn mk-tmp-dir [dirname]
  (let [basedir (System/getProperty "java.io.tmpdir")
        tmpname (str basedir "/" (rand-int 10000) dirname)
        dir (io/file tmpname)]
    (when-not (.mkdirs dir)
      (throw (Exception. "Unable to create dir " tmpname)))
    dir))
