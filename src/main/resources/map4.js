function() {

    var block = this.value;
    for(var ontology in block) {
        var points = block[ontology];
        for(var i=0; i<points.length; i++) {
            var point = points[i];
            var terms = point.data;
            for(var j=0; j<terms.length; j++) {
                var term = terms[j];
                if (term.type == "string") {
                    emit(this._id, term.value);
                }
            }
        }
    }

}