(defproject gargamel "0.5.0-SNAPSHOT"
  :description "Generates changelog between two git refs."
  :url "https://github.com/MailOnline/gargamel"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[stencil "0.3.2"]
                 [org.clojure/tools.cli "0.3.1"]
                 [cheshire "5.3.1"]]
  :profiles {:uberjar {:main gargamel.core
                       :aot :all}})
