#lang plai-typed
(print-only-errors #t)

;; We want functions! Give us functions!

#|
(define (double x) (+ x x))
(define (quadruple x) (double (double x)))
(define (const5 _) 5)
|#

(define-type FunDefC
  [fdC (name : symbol) (arg : symbol) (body : ExprC)])

;; ExprC =
;; | num
;; | (+ ExprC ExprC)
;; | (* ExprC ExprC)
;; | id
;; | (id ExprC)

(define-type ExprC
  [numC (val : number)]
  [addC (lhs : ExprC) (rhs : ExprC)]
  [multC (lhs : ExprC) (rhs : ExprC)]
  [idC (id : symbol)]
  [appC (fun : symbol) (arg : ExprC)]
  )

;; parse : s-exp -> ExprC
(define (parse se)
  (cond
    [(s-exp-number? se)
     (numC (s-exp->number se))]
    [(and (s-exp-list? se) (= (length (s-exp->list se)) 3)
          (equal? '+ (s-exp->symbol (first (s-exp->list se)))))
     (addC
      (parse (second (s-exp->list se)))
      (parse (third (s-exp->list se))))]
    [(and (s-exp-list? se) (= (length (s-exp->list se)) 3)
          (equal? '* (s-exp->symbol (first (s-exp->list se)))))
     (multC
      (parse (second (s-exp->list se)))
      (parse (third (s-exp->list se))))]
    [(s-exp-symbol? se)
     (idC (s-exp->symbol se))]
    [(and (s-exp-list? se) (= (length (s-exp->list se)) 2)
          (s-exp-symbol? (first (s-exp->list se))))
     (appC
      (s-exp->symbol (first (s-exp->list se)))
      (parse (second (s-exp->list se))))]
    [else
     (error 'parse "You are wrong")]))

(test (parse '23)
      (numC 23))
(test (parse '(+ 23 5))
      (addC (numC 23) (numC 5)))
(test (parse '(+ 23 (+ 23 5)))
      (addC (numC 23)
            (addC (numC 23) (numC 5))))
(test (parse '(* 23 5))
      (multC (numC 23) (numC 5)))
(test (parse (symbol->s-exp 'x))
      (idC 'x))
(test (parse '(double 13))
      (appC 'double (numC 13)))

;; subst : ExprC symbol ExprC -> ExprC
(define (subst what for in)
  (type-case
      ExprC in
    [numC
     (val)
     in]
    [addC
     (lhs rhs)
     (addC (subst what for lhs)
           (subst what for rhs))]
    [multC
     (lhs rhs)
     (multC (subst what for lhs)
            (subst what for rhs))]
    [idC
     (id)
     (cond [(symbol=? id for)  what]
           [else  in])]
    [appC
     (fun arg)
     (appC fun (subst what for arg))
     ]))

(test (subst (numC 13) 'x (idC 'x))
      (numC 13))
(test (subst (numC 13) 'x (idC 'y))
      (idC 'y))
(test (subst (numC 13) 'x (numC 5))
      (numC 5))
(test (subst (numC 13) 'x (addC (idC 'x) (idC 'x)))
      (addC (numC 13) (numC 13)))
(test (subst (numC 13) 'x (addC (idC 'x) (idC 'y)))
      (addC (numC 13) (idC 'y)))
(test (subst (numC 13) 'x (addC (idC 'x) (appC 'double (idC 'x))))
      (addC (numC 13) (appC 'double (numC 13))))
(test (subst (numC 13) 'x (multC (idC 'x) (idC 'x)))
      (multC (numC 13) (numC 13)))
(test (subst (numC 13) 'x (appC 'double (idC 'x)))
      (appC 'double (numC 13)))
(test (subst (numC 13) 'x (appC 'double (appC 'double (idC 'x))))
      (appC 'double (appC 'double (numC 13))))

;; get-fundef : symbol (listof FunDefC) -> FunDefC
(define (get-fundef name fds)
  (cond [(empty? fds)  (error 'get-fundef "unknown function")]
        [(symbol=? name (fdC-name (first fds)))  (first fds)]
        [else  (get-fundef name (rest fds))]))

;; interp : ExprC (listof FunDefC) -> number
(define (interp p fds)
  (type-case
      ExprC p
    [numC
     (val)
     val]
    [addC
     (lhs rhs)
     (+ (interp lhs fds) (interp rhs fds))]
    [multC
     (lhs rhs)
     (* (interp lhs fds) (interp rhs fds))]
    [idC
     (id)
     (error 'interp "undefined")]
    [appC
     (fun arg)
     (let ([fd  (get-fundef fun fds)])
       (interp (subst arg (fdC-arg fd) (fdC-body fd)) fds))
       ]
    ))

;; Testing begins here!

(define fundefs
  (list (fdC 'double 'x (addC (idC 'x) (idC 'x)))
        (fdC 'quadruple 'x (appC 'double (appC 'double (idC 'x))))
        (fdC 'const5 '_ (numC 5))))

;; interp* : s-exp -> number
(define (interp* s)
  (interp (parse s) fundefs))

(test (interp* '23)
      23)
(test (interp* '(+ 23 5))
      28)
(test (interp* '(+ 23 (+ 23 5)))
      51)
(test (interp* '(* 23 5))
      115)
(test (interp* '(const5 13))
      (interp* '5))
(test (interp* '(double 13))
      (interp* '(+ 13 13)))
(test (interp* '(quadruple 13))
      (interp* '(double (double 13))))

(test/exn (interp* '(+ x 5))
          "undefined")
