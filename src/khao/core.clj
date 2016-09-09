(ns khao.core
  (:require [alandipert.enduro :as e]))

(defn max-of [k coll]
  (if (seq coll)
    (apply max (map k coll))
    nil))

(def S (e/file-atom {:restaurants {} :eats []} "data/state.clj"))

(defn add-restaurant [state rkey name type]
  (e/swap! state update :restaurants
           assoc rkey {:id rkey :name name :type type}))

(defn add-eat [state rkey ate-at]
  (e/swap! state update :eats
           (fn [eats]
             (let [next-id (inc (or (max-of :id eats) 0))]
               (conj eats {:id next-id :restaurant rkey :ate-at ate-at})))))

(defn former-eats [eats]
  (sort-by :ate-at eats))

(defn format-eat [{:keys [restaurant ate-at]} restaurants]
  (let [{:keys [name]} (get restaurants restaurant)]
    (str name ":         " ate-at)))

(defn recommend-restaurants [{:keys [eats restaurants]}]
  (map #(format-eat % restaurants) (former-eats eats)))

(comment
  @S
  (add-restaurant S :shop "KFC" :western/fast-food)
  (add-eat S :shop #inst "2016-09-05T19:40")
  (recommend-restaurants @S))
