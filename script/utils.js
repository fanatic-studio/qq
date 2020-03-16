module.exports = {
    count(obj) {
        try {
            if (typeof obj === "undefined") {
                return 0;
            }
            if (typeof obj === "number") {
                obj += "";
            }
            if (typeof obj.length === 'number') {
                return obj.length;
            } else {
                let i = 0, key;
                for (key in obj) {
                    i++;
                }
                return i;
            }
        } catch (e) {
            return 0;
        }
    },

    getObject(obj, keys) {
        let object = obj;
        if (this.count(obj) > 0 && this.count(keys) > 0) {
            let arr = keys.replace(/,/g, "|").replace(/\./g, "|").split("|");
            arr.some((key) => {
                if (typeof object[key] === "undefined") {
                    object = null;
                    return true;
                }
                object = object[key];
            })
        }
        return object;
    },
};
