
package com.jdbcinterop.dsl

import com.jdbcinterop.core.QueryBuilder

package object interpolation {
  implicit class SQLHelper(val sc: StringContext) extends AnyVal {
    // Special case: No arguments 
    def sql(): QueryBuilder = {
      QueryBuilder.empty.addLiteral(sc.parts.mkString(""))
    }

    def sql[A](a: A): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B](a: A, b: B): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C](a: A, b: B, c: C): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D](a: A, b: B, c: C, d: D): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E](a: A, b: B, c: C, d: D, e: E): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F](a: A, b: B, c: C, d: D, e: E, f: F): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G](a: A, b: B, c: C, d: D, e: E, f: F, g: G): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M, N](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      builder.addLiteral(strings.next())
      builder.addVariable[S](s)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      builder.addLiteral(strings.next())
      builder.addVariable[S](s)
      builder.addLiteral(strings.next())
      builder.addVariable[T](t)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      builder.addLiteral(strings.next())
      builder.addVariable[S](s)
      builder.addLiteral(strings.next())
      builder.addVariable[T](t)
      builder.addLiteral(strings.next())
      builder.addVariable[U](u)
      builder.addLiteral(strings.next())
      builder
    }

    def sql[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V): QueryBuilder = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      builder.addLiteral(strings.next())
      builder.addVariable[S](s)
      builder.addLiteral(strings.next())
      builder.addVariable[T](t)
      builder.addLiteral(strings.next())
      builder.addVariable[U](u)
      builder.addLiteral(strings.next())
      builder.addVariable[V](v)
      builder.addLiteral(strings.next())
      builder
    }
  }
}