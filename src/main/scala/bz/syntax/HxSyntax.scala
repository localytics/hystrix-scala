package bz.syntax

import bz.HX
import bz.HxControl._
import bz.HxInterface
import com.netflix.hystrix.HystrixCommand.Setter
import com.netflix.hystrix.HystrixCommandProperties.{Setter => CSetter}
import scalaz.\/

/**
 * Implicit classes decorating functions and Setters
 * with methods for execution inside of a HystrixCommand
 */
object hx {

  /**
   * Implicit class decorating functions of arity 0
   */
  implicit def toHxLift[A](fn: () => A): HxLift[A] = new HxLift[A](fn)
  class HxLift[A](fn: () => A) {

    /**
     * Lift a function of arity 0 into a
     * HystrixCommand and execute using
     * the given Setter
     */
    def liftHx[M[_]](s: Setter)(implicit hxi: HxInterface[M]): M[A] =
      hxi.mHx(s)(fn)
  }

  /**
   * Implicit class decorating a HystrixCommand.Setter
   */

  implicit def toGKLift(s: Setter): GKLift = new GKLift(s)
  class GKLift(s: Setter){

    /**
     * Lift a Setter into a HystrixCommand with
     * run method of r and getFallback method of fb
     * and execute
     */
    def run[A](r: () => A, fb: () => A): A =
      HX.instance(r, fb)(s)

    /**
     * Lift a Setter into a HystrixCommand and
     * execute using the given () => A
     */
    def run[M[_], A](fn: () => A)(implicit hxi: HxInterface[M]): M[A] =
      hxi.mHx(s)(fn)

  }

  implicit def toGKLiftConfig(s: Setter): GKLiftConfig = new GKLiftConfig(s)
  class GKLiftConfig(s: Setter) {

    /**
     * Returns original Setter with added command properties,
     * as configured by the provided fn
     */
    def config(fn: CSetter => CSetter): Setter =
      s.andCommandPropertiesDefaults(fn(CSetter()))
  }

  /**
   * Implicit class decorating a HystrixCommand.Setter
   */

  implicit def toControlLift(s: Setter): ControlLift = new ControlLift(s)
  class ControlLift(s: Setter){

    /**
     * Lift a Setter into a HystrixCommand and
     * execute using the given () => HxResult[A]
     */
    def runC[A](fn: () => HxResult[A]): HxResult[A] =
      controlInterface(s)(fn)
  }

  /**
   * Implicit class decorating a HystrixCommandProperties.Setter
   */
  implicit def toCSetterLift(s: CSetter): CSetterLift = new CSetterLift(s)
  class CSetterLift(s: CSetter){
    import com.netflix.hystrix.HystrixCommandProperties
    import HystrixCommandProperties.ExecutionIsolationStrategy._

    /**
     * Use the Semaphore strategy for this Setter
     */
     def usingSemaphore(): CSetter =
      s.withExecutionIsolationStrategy(SEMAPHORE)

    /**
     * Use the Thread strategy for this Setter
     */
     def usingThreads(): CSetter =
      s.withExecutionIsolationStrategy(THREAD)
   }
}
