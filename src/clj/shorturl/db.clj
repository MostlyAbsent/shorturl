(ns shorturl.db
  (:require
   [clojure.java.jdbc :as j]
   [honey.sql :as sql]
   [honey.sql.helpers :as h]
   [shorturl.env :refer [env]]))

(def mysql-db {:dbtype "mysql"
               :host (env :HOST)
               :dbname (env :DBNAME)
               :user (env :USER)
               :password (env :PASSWORD)})

(defn query [q]
  (j/query mysql-db q))

(defn insert! [q]
  (j/db-do-prepared mysql-db q))

(defn insert-redirect! [slug url]
  (insert!
   (-> (h/insert-into :redirects)
       (h/columns :slug :url)
       (h/values [[slug url]])
       (sql/format))))

(defn get-url [slug]
  (-> (query
       (-> (h/select :url)
           (h/from :redirects)
           (h/where [:= :slug slug])
           (sql/format)))
      first
      :url))

(comment
  (query
   (-> (h/select :*)
       (h/from :redirects)
       (sql/format)))
  (insert!
   (-> (h/insert-into :redirects)
       (h/columns :slug :url)
       (h/values [["abc" "https://github.com/seancorfield/honeysql?search=1"]])
       (sql/format)))
  (insert-redirect! "snotehu" "https://github.com/clojure/java.jdbc")
  (get-url "abc"))
