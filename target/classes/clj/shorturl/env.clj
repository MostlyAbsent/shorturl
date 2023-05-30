(ns shorturl.env)

(def envvar (clojure.edn/read-string (slurp "env.edn")))

(defn env [k]
  (or (k envvar)
      (System/getenv (name k))))
