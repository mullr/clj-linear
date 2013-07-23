(ns clj-linear.commons-math
  (:require [clojure.set :as set]
            [clj-linear.expression :as expr])
  (:import [org.apache.commons.math3.optim OptimizationData]
           [org.apache.commons.math3.optim.linear
            LinearObjectiveFunction LinearConstraint NonNegativeConstraint
            LinearConstraintSet SimplexSolver Relationship]
           [org.apache.commons.math3.optim.nonlinear.scalar GoalType]
           [org.apache.commons.math3.optim PointValuePair]))

;; ## solver implementation using apache commons math

(defn solve [objective constraints]
  (-> (.optimize (SimplexSolver.) 
                 (into-array OptimizationData
                             [objective 
                              (LinearConstraintSet. constraints) 
                              GoalType/MINIMIZE 
                              (NonNegativeConstraint. false)]))
      (.getPoint)
      (vec)))

(defn to-double-array
  "Convert a sparse vector (map of index to value) into a double
   array, with unfilled values set to zero. Regular vectors are 
   converted directly. "
  [vec-or-map]
  (cond
   (vector? vec-or-map) (double-array vec-or-map)
   :default
   (let [len (-> vec-or-map keys sort last inc)
         empty-vec (vec (repeat len 0.0))
         double-vec (reduce (fn [v entry] (assoc v (first entry) 
                                                 (+ (nth v (first entry))
                                                    (second entry )))) 
                            empty-vec
                            vec-or-map)]
     (double-array double-vec))))

(def relationship-kw
  {:>= Relationship/GEQ
   :<= Relationship/LEQ
   :=  Relationship/EQ})

(defn make-constraint [lhs-coefficients relationship rhs-constant]
  (let [r (relationship-kw relationship)]
    (assert r "relationship must be defined")
    (LinearConstraint. (to-double-array lhs-coefficients)
                       r
                       (double rhs-constant))))

(defn objective [coefficients constant-term]
  (LinearObjectiveFunction. (to-double-array coefficients) (double constant-term)))

(defn allocate-columns
  "Given a seq of expressions (sym -> coefficient maps), 
   choose a unique index for each symbol."
  [exprs]
  (let [syms (distinct (mapcat keys exprs))]
    (zipmap syms (range (count syms)))))


(defn coefficient-vector
  "Turn an expression into a dense coefficient vector suitable for use
  with commons math, using the columns given in column-allocation."
  [expr column-allocation]
  (reduce (fn [v [variable coefficient]]
            (assoc v (column-allocation variable) coefficient))
          (into [] (repeat (count column-allocation) 0))
          expr))

(defn make-solution-map
  "Turn a vector of solutions into a map, using the given column allocation"
  [solution-vector column-allocation]
  (reduce (fn [m [symbol position]] 
            (assoc m symbol (solution-vector position)))
          {} column-allocation))

(defn minimize [objective-expr constraints]
  (let [all-exprs (-> (map :lhs constraints) (conj objective-expr))
        column-allocation (allocate-columns all-exprs)

        objective-coefficients (coefficient-vector
                                (expr/without-constant objective-expr)
                                column-allocation)
        objective (objective objective-coefficients (expr/constant objective-expr))

        tableau (for [c constraints]
                  [(coefficient-vector (:lhs c) column-allocation)
                   (:op c)
                   (expr/constant (:rhs c))])

        constraints (map (partial apply make-constraint) tableau)
        solution (solve objective constraints)]
    (-> solution 
        (make-solution-map column-allocation)
        (dissoc nil))))
