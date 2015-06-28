function(key, values) {
	var ar=values.concat();
	var ret="";
	if (ar.length > 1) {
		ret=ar[0];
		for(var i=1;i<ar.length;i++){
			ret= ret + "," + ar[i];
		}
		return ret;
	} else if (ar.length == 1){
		ret = ar[0];
		return ret;
	} else {
		return ar;
	}

}