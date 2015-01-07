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

(deftest test-adding-extra-section
  (intern 'leiningen.gargamel 'project-config
          {:sections [{:key :refactor :regex ".*refactor.*" :title "Refactorings, improvements"}
                      {:key :technical :regex ".*#(\\d+).*" :title "Internal changes"}
                      {:key :business :regex ".*(MOL-\\d+).*" :title  "Business related changes"}
                      {:key :geo :regex ".*Geo.*" :title  "Geo related changes"}]})

  (let [tmpdir (create-temp-dir "gargamel-test")]
    (gargamel-changelog "clj_fe" (str tmpdir) "foobardir" "66e2387" "67f9576")

    (let [changelog (slurp (str tmpdir "/changelog-66e2387-67f9576.html"))
          assert-changelog (slurp (io/resource "changelog-66e2387-67f9576-geosection.html"))]

      (is (= assert-changelog changelog) "html output for changelog is not identical")))
  ;; tear down
  (intern 'leiningen.gargamel 'project-config nil))

(deftest test-adding-extra-link
  (intern 'leiningen.gargamel 'project-config
          {:sections [{:key :refactor :regex ".*refactor.*" :title "Refactorings, improvements"}
                      {:key :technical :regex ".*#(\\d+).*" :title "Internal changes"}
                      {:key :business :regex ".*(MOL-\\d+).*" :title  "Business related changes"}
                      {:key :geo :regex ".*Geo.*" :title  "Geo related changes"}]
           :linkable-objects
           [{:template "<a href=\"https://github.com/%1$s/%2$s/issues/$1\"> #$1</a>" :regex "#(\\d+)"}
            {:template "<a href=\"https://github.com/$2/issues/$3\">$2: $3</a>" :regex "(([_\\w/-]+)#(\\d+))"}
            {:template "<a href=\"http://andjira.and.dmgt.net:8080/browse/$1\">$1</a>" :regex "(MOL-\\d+)"}
            {:template "<a href=\"https://github.com/%1$s/%2$s/blob/master/README.md\">README</a>" :regex "README"}
            {:template "<br/>" :regex "(\\n)"}]})

  (let [tmpdir (create-temp-dir "gargamel-test")]
    (gargamel-changelog "clj_fe" (str tmpdir) "foobardir" "66e2387" "67f9576")

    (let [changelog (slurp (str tmpdir "/changelog-66e2387-67f9576.html"))
          assert-changelog (slurp (io/resource "changelog-66e2387-67f9576-readmelink.html"))]

      (is (= assert-changelog changelog) "html output for changelog is not identical")))
  ;; tear down
  (intern 'leiningen.gargamel 'project-config nil))
