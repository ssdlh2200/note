# c operator precedence

<table border="1" cellspacing="0" cellpadding="5" style="color:black">
  <thead>
    <tr>
      <th>Precedence</th>
      <th>Operator</th>
      <th>Description</th>
      <th>Associativity</th>
    </tr>
  </thead>
  <tbody>
    <tr style="background-color:#ffcccc;">
      <td>1</td>
      <td>++ --</td>
      <td>Suffix/postfix increment and decrement</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffcccc;">
      <td>1</td>
      <td>()</td>
      <td>Function call</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffcccc;">
      <td>1</td>
      <td>[]</td>
      <td>Array subscripting</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffcccc;">
      <td>1</td>
      <td>.</td>
      <td>Structure and union member access</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffcccc;">
      <td>1</td>
      <td>-></td>
      <td>Structure and union member access through pointer</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffcccc;">
      <td>1</td>
      <td>(type){list}</td>
      <td>Compound literal (C99)</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ff9999;">
      <td>2</td>
      <td>++ --</td>
      <td>Prefix increment and decrement</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#ff9999;">
      <td>2</td>
      <td>+ -</td>
      <td>Unary plus and minus</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#ff9999;">
      <td>2</td>
      <td>! ~</td>
      <td>Logical NOT and bitwise NOT</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#ff9999;">
      <td>2</td>
      <td>(type)</td>
      <td>Cast</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#ff9999;">
      <td>2</td>
      <td>*</td>
      <td>Indirection (dereference)</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#ff9999;">
      <td>2</td>
      <td>&</td>
      <td>Address-of</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#ff9999;">
      <td>2</td>
      <td>sizeof</td>
      <td>Size-of</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#ff9999;">
      <td>2</td>
      <td>_Alignof</td>
      <td>Alignment requirement (C11)</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#ff6666;">
      <td>3</td>
      <td>* / %</td>
      <td>Multiplication, division, and remainder</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ff6666;">
      <td>4</td>
      <td>+ -</td>
      <td>Addition and subtraction</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ff6666;">
      <td>5</td>
      <td>&lt;&lt; &gt;&gt;</td>
      <td>Bitwise left shift and right shift</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffcc99;">
      <td>6</td>
      <td>&lt; &lt;=</td>
      <td>Relational operators &lt; and ≤</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffcc99;">
      <td>6</td>
      <td>&gt; &gt;=</td>
      <td>Relational operators &gt; and ≥</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffcc99;">
      <td>7</td>
      <td>== !=</td>
      <td>Equality operators</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffff99;">
      <td>8</td>
      <td>&amp;</td>
      <td>Bitwise AND</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffff99;">
      <td>9</td>
      <td>^</td>
      <td>Bitwise XOR (exclusive or)</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ffff99;">
      <td>10</td>
      <td>|</td>
      <td>Bitwise OR (inclusive or)</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ccff99;">
      <td>11</td>
      <td>&amp;&amp;</td>
      <td>Logical AND</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#ccff99;">
      <td>12</td>
      <td>||</td>
      <td>Logical OR</td>
      <td>Left-to-right</td>
    </tr>
    <tr style="background-color:#99ff99;">
      <td>13</td>
      <td>?:</td>
      <td>Ternary conditional</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#99ffcc;">
      <td>14</td>
      <td>= += -= *= /= %= &lt;&lt;= &gt;&gt;= &amp;= ^= |=</td>
      <td>Assignment operators</td>
      <td>Right-to-left</td>
    </tr>
    <tr style="background-color:#99ffff;">
      <td>15</td>
      <td>,</td>
      <td>Comma</td>
      <td>Left-to-right</td>
    </tr>
  </tbody>
</table>

