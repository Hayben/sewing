function() {
    var array = this.value.links;
    for(var i=0; i<array.length; i++) {
        var item = array[i];
        emit(item, this.value);
    }
}