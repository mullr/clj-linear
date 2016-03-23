# clj-linear

A nice clojure api for doing linear programming. It uses Apache Commons Math under the covers. 

[![Build Status](https://travis-ci.org/mullr/clj-linear.svg?branch=master)](https://travis-ci.org/mullr/clj-linear)


## Usage

Put this in your lein depenencies:
```clojure
[clj-linear "0.1.0-notyet"]
```

Then add this to your requires:
```clojure
  [clj-linear.core :as lp]
```

Then you can use it like this:
```clojure
(lp/minimize
  (lp/expression (+ (* -2 :x) :y -5))
  (lp/constraints 
    (<= (+ :x (* 2 :y)) 6)
    (<= (+ (* 3 :x) (* 2 :y)) 12)
    (>= :y 0)))

;; => {:x 4.0, :y 0.0}
```

## Details

clj-linear uses two kinds of data under the covers. 

### Expressions

An linear expression is a map of variables to coefficients. You can make it by hand, or with the ```expression```
macro. ```clj-linear.expressions``` has some useful code for manipulating linear expressions in this form. 

### Constraints

Constraint equations are maps of the form ```{:op :lhs :rhs}```, where ```:op``` is one of ```:=```, 
```:>=```, or ```:<=```. ```:lhs``` and ```:rhs``` are both linear expressions as described above. 
These are most easily created with the ```constraint``` and ```constraints``` macros, but
may be manipulated by hand as well. NB: the macros will automatically normalize your constraints to put
all the variables on the left-hand side and the constant on the right. The input to the minimize function must
be in this form. 


### Tips

You can you anything that's not a number or map as the variable in your linear systems. In particular, it's 
useful build up a system using vectors as variables and later merge the results into your own data using 
```assoc-in``` .

## License

Copyright © 2013 Russell Mull

Distributed under the Eclipse Public License, the same as Clojure.
