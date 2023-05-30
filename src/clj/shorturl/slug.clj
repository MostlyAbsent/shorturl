(ns shorturl.slug)

(def charset "abcdefghijklmnopqrstuvwxyz")

(defn generate-slug []
  (->> (repeatedly #(rand-nth charset))
       (take 4)
       (apply str)))
