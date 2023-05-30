(ns shorturl.env)

(def envvar
  (if (.exists (clojure.java.io/file "env.edn")
               (clojure.edn/read-string (slurp "env.edn"))
               {})))

(defn env [k]
  (or (k envvar)
      (System/getenv (name k))))
