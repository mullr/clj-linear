(ns clj-linear.builder
  (:require [clj-linear.expression :as expr]))

(defn- normalize-constraint
  "Normalize the constraint equation to have variables on the left and a
  constant term on the right"
  [c]
  (let [combined (expr/- (:lhs c) (:rhs c))
        constant (expr/constant combined)]
    {:op (:op c)
     :lhs (expr/without-constant combined)
     :rhs {nil (- constant)}}))

(defn term
  "Coerce a value, or a pair of values, into an expression term."
  ([x]
   (cond
     (map? x) x
     (number? x) {nil x}
     :else {x 1}))
  ([x y]
   (cond
     (number? x) {y x}
     (number? y) {x y})))

(declare plus)

(defn expression
  "Coerce a value to an expression"
  [x]
  (if (map? x)
    x
    (plus x)))

(defn make-constraint
  "Create a normalized constraint equation with the given operation,
  left-hand, and right-hand sides. "
  [op lhs rhs]
  (normalize-constraint
   {:op op
    :lhs (expression lhs)
    :rhs (expression rhs)}))

;;; Expression and constraint building (used by the expression macro)

(defn multiply [x y]
  (if (and (number? x) (number? y))
    (term (* x y))
    (term x y)))

(defn minus [a] (multiply a -1))

(defn plus [& args]
  (->> args (map term) (reduce expr/+) expr/remove-zero-terms)) 

(defn equals [lhs rhs] (make-constraint := lhs rhs))
(defn geq [lhs rhs] (make-constraint :>= lhs rhs)) 
(defn leq [lhs rhs] (make-constraint :<= lhs rhs))
