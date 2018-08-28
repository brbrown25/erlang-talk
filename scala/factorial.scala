def factorial(n: BigInt): BigInt = { 
  if (n == 0) 1
  else n * factorial(n - 1)
}

@annotation.tailrec
def factorialRecursive(n: BigInt, result: BigInt = 1): BigInt = { 
  if (n == 0) result
  else factorialRecursive(n - 1, result * n)
}

// def factorial(n: BigInt): BigInt = { 
//   @annotation.tailrec
//   def fac(curr: BigInt, res: BigInt): BigInt = {
//     if (curr == 0) res
//     else fac(curr - 1, res * curr)
//   }

//   fac(n, 1)
// }