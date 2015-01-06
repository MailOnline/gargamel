(defproject gargamel "0.5.0-SNAPSHOT"
  :description "Generates changelog between two git refs."
  :url "https://github.com/MailOnline/gargamel"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [stencil "0.3.2" :exclusions [org.clojure/clojure]]
                 [org.clojure/tools.cli "0.3.1":exclusions [org.clojure/clojure]]
                 [cheshire "5.3.1":exclusions [org.clojure/clojure]]]
  :profiles {:uberjar {:main gargamel.core
                       :aot :all}
             :test {:resource-paths ["test-resources"]}})
