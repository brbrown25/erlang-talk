trait UOM
case object centimeter extends UOM
case object inch extends UOM

// basic pattern match
def convert_length(value: (UOM, Double)): (UOM, Double) = {
  val (u, d) = value
  u match {
    case centimeter => (u, (d / 2.54))
    case inch => (u, (d * 2.54))
  }
}

// // with guards
// def convert_length(value: (UOM, Double)): (UOM, Double) = {
//   value match {
//     case (u:UOM, d: Double) if u == centimeter => (u, (d / 2.54))
//     case (u:UOM, d: Double) if u == inch => (u, (d * 2.54))
//   }
// }