<html>
<head>
<title>Items -- Grocery Store</title>
<script language="javascript">
function valQty()
{
  var ReturnVal1 = true;
  var available = parseFloat(FormItemDesc.qtyAvailable.value);
  var qty = parseFloat(FormItemDesc.orderQty.value);
  var MaxDefault = 50;

  if (FormItemDesc.orderQty.value == "")
  {
    alert ("You must enter a Quantity to add to your Cart");
    ReturnVal1 = false;
  }
  else {

 	if (isNaN(FormItemDesc.orderQty.value) == true)
 	{
 	   alert ("The entered quantity is not a valid value");
	   ReturnVal1 = false;

    } else {

  	      if (available > MaxDefault)
	      {
	          maxAmount = 50;

	      } else  {
	        maxAmount = available;

	      }

          if (qty == 0)
          {
              alert ("You need to enter an amount greater than zero");
              ReturnVal1 = false;
          }

          if (qty > maxAmount)
          {
     	      alert ("Maximum quantity you can order for this item is: " + maxAmount);
              ReturnVal1 = false;
          }
    }

  }
  return ReturnVal1;
}

</script>
</head>

<body>
<h2> Order an Item </h2>

<?php
$Param = $_GET['RQ'];
$link = mysql_connect("rerun","potiro","pcXZb(kL")
or die ("Could not connect to Server:".mysql_error());

mysql_select_db("poti",$link)
or die ("Unable to connect to Database ".mysql_error());


    $query_string = "select * from products where product_id = $Param";


$result = mysql_query($query_string);

$num_rows = mysql_num_rows($result);


if ($num_rows > 0 ) {
	print "<form name='FormItemDesc' method='post' action='cart.php' target='rbottom' onsubmit='return valQty()'>";
	print "<table border=1 cellpadding='4' cellspacing='4' valign='middle' text-align='center'>";
	print "<tr align=center>\n";
	print "<th border = '1'>Product Name</th>";
	print "<th border = '1'>Unit Quantity</th>";
	print "<th border = '1'>Unit Price</th>";
	print "<th border = '1'>Quantity in Stock</th>";
	print "<th border = '1'>Order Quantity</th>";
	print "</tr>";

    while ( $a_row = mysql_fetch_assoc($result) ) {
         print "<tr align=center>\n";
         print "<td border = '1'>$a_row[product_name]</td>";
         print "<td border = '1'>$a_row[unit_quantity]</td>";
         print "<td border = '1'>$a_row[unit_price]</td>";
         print "<td border = '1'>$a_row[in_stock]</td>";
		 print "<td border = '1'><input type='text' name='orderQty' size='20'></td>";
		 print "<input type='hidden' name='orderItemID' value='$a_row[product_id]'>";
		 print "<input type='hidden' name='orderItemName' value='$a_row[product_name]'>";
		 print "<input type='hidden' name='orderPrice'  value='$a_row[unit_price]'>";
		 print "<input type='hidden' name='qtyAvailable' value='$a_row[in_stock]'>";
		 print "<input type='hidden' name='orderUQty' value='$a_row[unit_quantity]'>";
         print "</tr>";
    }
    print "</table>";
	print "<p><input type='submit' value= 'Add to Shopping Cart'></p>";
    print "</form>";
} else {

	print "<p> Please Select an item from the products on the left hand side.</p>";
}


mysql_close($link);
?>
</body>
</html>

