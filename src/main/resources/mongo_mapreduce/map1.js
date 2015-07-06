function(){
	var id = this._id.str;
	if ("ontology" in this) {
		var ontology = JSON.parse(this["ontology"]);
		for(var key in ontology) {
			emit(key, id);
		}
	}
}
