(ns clj-linear.core
  (:require [clojure.walk :as walk]
            [clj-linear.expression :as expr]
            [clj-linear.commons-math :as commons-math]
            [clj-linear.builder :as builder]))

(defn- replace-operators [e]
  (walk/postwalk-replace
   {'= `builder/equals
    '>= `builder/geq
    '<= `builder/leq
    '+ `builder/plus
    '- `builder/minus
    '* `builder/multiply
    'clojure.core/= `builder/equals
    'clojure.core/>= `builder/geq
    'clojure.core/<= `builder/leq
    'clojure.core/+ `builder/plus
    'clojure.core/- `builder/minus
    'clojure.core/* `builder/multiply}
   e))

(defmacro expression
  "Make a linear expression using regular clojure operators"
  [e]
  `(builder/expression ~(replace-operators e)))

(defmacro constraint
  "Make a constraint using regular clojure oparators."
  [a]
  (replace-operators a))

(defmacro constraints
  "Create multiple constraints "
  [& args]
  (into #{} (for [a args]
              `(constraint ~a))))

(defn minimize [objective constraints]
  (commons-math/minimize objective constraints))
