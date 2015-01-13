(defproject gargamel "0.5.0"
  :description "Generates changelog between two git refs."
  :url "https://github.com/MailOnline/gargamel"
  :license {:name "GNU General Public License, version 3"
            :url "http://opensource.org/licenses/GPL-3.0"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [stencil "0.3.2" :exclusions [org.clojure/clojure]]
                 [org.clojure/tools.cli "0.3.1":exclusions [org.clojure/clojure]]
                 [cheshire "5.3.1":exclusions [org.clojure/clojure]]]
  :profiles {:uberjar {:main gargamel.core
                       :aot :all}
             :test {:resource-paths ["test-resources"]}})
