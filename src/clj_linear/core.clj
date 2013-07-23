(ns clj-linear.core
  (:require [clojure.walk :as walk]
            [clj-linear.expression :as expr]
            [clj-linear.commons-math :as commons-math]
            [clj-linear.builder :as builder]))

(defn- replace-operators [e]
  (walk/postwalk-replace 
   {'=  builder/equals    'clojure.core/=  builder/equals
    '>= builder/geq       'clojure.core/>= builder/geq
    '<= builder/leq       'clojure.core/<= builder/leq
    '+  builder/plus      'clojure.core/+  builder/plus
    '-  builder/minus     'clojure.core/-  builder/minus
    '*  builder/times     'clojure.core/*  builder/times}
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
