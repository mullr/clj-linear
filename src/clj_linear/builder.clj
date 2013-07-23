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

;; A protocol for turning objects into expression terms.
(defprotocol ExpressionTerm
  (term [this] [this second-val]))

(extend-protocol ExpressionTerm
  clojure.lang.PersistentArrayMap
  (term [this] this)

  java.lang.Number
  (term
    ([this] {nil this})
    ([this variable] {variable this}))

  java.lang.Object
  (term
    ([this] {this 1})
    ([this number] {this number})))

;; Polymorphic fn to coerce a value into an expression
(defprotocol Expression
  (expression [this]))

(declare plus)

(extend-protocol Expression
  clojure.lang.PersistentArrayMap
  (expression [this] this)

  java.lang.Object
  (expression [this] (plus this)))

(defn make-constraint
  "Create a normalized constraint equation with the given operation,
  left-hand, and right-hand sides. "
  [op lhs rhs]
  (normalize-constraint 
   {:op op
    :lhs (expression lhs)
    :rhs (expression rhs)}))

;; ### Functions for building expressions and constraints
;; These are used by the eqn macro

(defprotocol Times
  (multiply [this second-val]))

(extend-protocol Times
  java.lang.Number
  (multiply [this second-val] 
    (if (number? second-val)
      (term (* this second-val))
      (term this second-val)))
  
  java.lang.Object
  (multiply [this second-val]
    (term this second-val)))

(defn minus [a] (multiply a -1))

(defn plus [& args]
  (->> args (map term) (reduce expr/+) expr/remove-zero-terms)) 

(defn equals [lhs rhs] (make-constraint := lhs rhs))
(defn geq [lhs rhs] (make-constraint :>= lhs rhs)) 
(defn leq [lhs rhs] (make-constraint :<= lhs rhs))
