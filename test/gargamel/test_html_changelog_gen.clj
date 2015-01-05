(ns gargamel.test-html-changelog-gen
  (:require [clojure.test :refer :all]
            [leiningen.gargamel :refer :all]
            [clojure.java.io :as io])
  (:import java.io.File))

(defn- create-temp-dir
  "Creates and returns a new temporary directory java.io.File."
  [name]
  (let [temp-file (File/createTempFile name nil)]
    (.delete temp-file)
    (.mkdirs temp-file)
    temp-file))

(intern 'gargamel.git 'changelog (fn [from to dir] (slurp (io/resource "raw-cl-66e2387..67f9576.txt"))))

(intern 'gargamel.git 'remote-url (fn [source-dir] "git@github.com:MailOnline/clj_fe.git"))

(deftest test-html-gen
  (let [tmpdir (create-temp-dir "gargamel-test")]
    (gargamel-changelog "clj_fe" (str tmpdir) "foobardir" "66e2387" "67f9576")

    (let [changelog (slurp (str tmpdir "/changelog-66e2387-67f9576.html"))
          assert-changelog (slurp (io/resource "changelog-66e2387-67f9576.html"))]

      (is (= assert-changelog changelog) "html output for changelog is not identical"))))
