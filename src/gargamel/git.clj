(ns gargamel.git
  (:require [clojure.java.shell :as sh]
            [gargamel.util :as util]
            [clojure.string :as str]))

(defn changelog [from to dir]
  (:out (sh/sh "git" "log" "--date=short" "--format=%h%;%cN;%d;%ad;%s;%b;" (format "%s..%s" from to) :dir dir)))

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

(defn project-name
  "figures out project name based on remote origin url"
  [rurl]
  (-> rurl
      (str/split #"/")
      last
      (str/replace ".git" "")
      str/trim))

(defn org-or-username
  "figures out organisation or gitusername based on remote origin url"
  [rurl]
  (let [https-prefix "https://"
        url (if (.startsWith rurl https-prefix)
              (str/replace rurl (re-pattern https-prefix) "")
              rurl)]
    (-> url
        (str/split #"[:/]")
        second)))

(defn c-url [source-dir project-name bb-url gh-url & args]
  (let [project-name (real-project-name project-name)
        r-url (remote-url source-dir)
        org-name (org-or-username r-url)
        url (if (re-find #"bitbucket" r-url) bb-url gh-url)]
    (apply (partial format url org-name project-name) args)))

(defn commit-url [source-dir project-name hash]
  (c-url source-dir project-name "https://bitbucket.org/%s/%s/commits/%s" "https://github.com/%s/%s/commit/%s" hash))

(defn compare-url [source-dir project-name from to]
    (c-url source-dir project-name "https://bitbucket.org/%s/%s/branches/compare/%s..%s" "http://github.com/%s/%s/compare/%s...%s" from to))
