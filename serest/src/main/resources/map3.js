function(){
	var id = this._id;
	var seed=this.seed;
	if ("ontology" in this) {
		var ontology = JSON.parse(this["ontology"]);
		for(var key in ontology) {
			emit(key, seed+":"+id);
		}
	}
}
