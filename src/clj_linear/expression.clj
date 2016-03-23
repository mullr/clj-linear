(ns clj-linear.expression
  (:refer-clojure :exclude [+ - *]))

;; Utilities for for manipulating linear expressions stored as a
;; variable -> coefficient map.

(defn- map-vals
  "Utility to map f over the values of map 'amap'."
  [f amap]
  (into {} (for [[key val] amap]
             [key (f val)])))

(defn coefficient
  "Get the coefficient for v in expr, or 0 if it doesn't exist. "
  [expr v]
  (or (get expr v) 0))

(defn without-constant
  "Get the expression without the constant term"
  [expr]
  (dissoc expr nil))

(defn constant
  "Get the constant part of expr."
  [expr]
  (coefficient expr nil))

(defn +
  "Add the expressions a and b."
  [a b]
  (merge-with clojure.core/+ a b))

(defn -
  "Negate or subtract expressions."
  ([e] (map-vals clojure.core/- e))
  ([a b] (+ a (- b))))

(defn *
  "Multiply expr by the given constant factor."
  [expr factor]
  (map-vals (partial clojure.core/* factor) expr))

(defn remove-zero-terms
  "Remove all entries from expr with a coefficient of 0."
  [expr]
  (into {} (filter #(not= 0 (val %)) expr)))

(defn substitute
  "Substitute the expression 'replacement' for the variable 'v' in expr."
  [expr v replacement]
  (remove-zero-terms
   (+ (dissoc expr v)
      (* replacement (coefficient expr v)))))

