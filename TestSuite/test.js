var should = require("should"),
  r = require("request");

describe('Lab 3 Test Suite', function () {
  describe('GET Method - Retrieve', function () {
    it('GET /library/catalog/ should return 200', function (done) {
      r('http://localhost:6789/library/catalog', function (err, res, body) {
        res.statusCode.should.equal(200);
        done();
      });
    });

    it('GET /library/catalog/book/bk101 should return 200', function (done) {
      r('http://localhost:6789/library/catalog/book/bk101', function (err, res, body) {
        res.statusCode.should.equal(200);
        done();
      });
    });

    it('GET /library/catalog/book/bk100 should return 404', function (done) {
      r('http://localhost:6789/library/catalog/book/bk100', function (err, res, body) {
        res.statusCode.should.equal(404);
        done();
      });
    });

    it('GET /library/catalog/book/bk104/author should return \'Corets, Eva\'', function (done) {
      r('http://localhost:6789/library/catalog/book/bk104/author', function (err, res, body) {
        res.statusCode.should.equal(200);
        body.should.equal('Corets, Eva');
        done();
      });
    });

    it('GET /library/catalog/book/bk105/publish_date should return correct \'2001-09-10\'', function (done) {
      r('http://localhost:6789/library/catalog/book/bk105/publish_date', function (err, res, body) {
        res.statusCode.should.equal(200);
        body.should.equal('2001-09-10');
        done();
      });
    });

    it('GET /Customers/Database/Customers should return \'YEEYHAHA\'', function (done) {
      r('http://localhost:6789/customers/Database/Customers', function (err, res, body) {
        res.statusCode.should.equal(200);
        body.should.equal('YEEYHAHA');
        done();
      });
    });

    it('GET /Customers/Database/Customers/Customer/GREAL should return text value', function (done) {
      r('http://localhost:6789/customers/Database/Customers/Customer/GREAL', function (err, res, body) {
        res.statusCode.should.equal(200);
        body.should.equal('OMGBOO THIS IS SO SICK');
        done();
      });
    });

    it('GET /Customers/Database/Customers/Customer/HUNGC/FullAddress/City should return \'Elgin\'', function (done) {
      r('http://localhost:6789/customers/Database/Customers/Customer/HUNGC/FullAddress/City', function (err, res, body) {
        res.statusCode.should.equal(200);
        body.should.equal('Elgin');
        done();
      });
    });

    it('GET /Customers/Database/Customersssss/ should return 404', function (done) {
      r('http://localhost:6789/customers/Database/Customersssss', function (err, res, body) {
        res.statusCode.should.equal(404);
        done();
      });
    });
  });

  describe('POST Method - Create', function () {
    it('POST /Customers/Database/Customers/ w/o body should return 405', function (done) {
      r.post('http://localhost:6789/customers/Database/Customers/Customer/GREAL?omg', function (err, res, body) {
        res.statusCode.should.equal(405);
        done();
      });
    });

    it('POST /Customers/Database/Customers/Customer w/o body should return 405', function (done) {
      r.post('http://localhost:6789/customers/Database/Customers/Customer/123?omg', function (err, res, body) {
        res.statusCode.should.equal(405);
        done();
      });
    });

    it('POST /Customers/Database/Customers/Customer w/o params should return 400', function (done) {
      var options = {
        url: 'http://localhost:6789/customers/Database/Customers/Customer/GREAL',
        body: 'omg'
      }
      r.post(options, function (err, res, body) {
        res.statusCode.should.equal(400);
        done();
      });
    });

    it('POST /Customers/Database/Customers/Customer/GREAL/new?hello should create node and return 201', function (done) {
      var options = {
        url: 'http://localhost:6789/customers/Database/Customers/Customer/GREAL/new?hello'
      }
      r.post(options, function (err, res, body) {
        res.statusCode.should.equal(201);
        done();
      });
    });

    it('POST /Customers/Database/Customers/Customer/GREAL/new?hello should already exist and return 405', function (done) {
      var options = {
        url: 'http://localhost:6789/customers/Database/Customers/Customer/GREAL/new?hello'
      }
      r.post(options, function (err, res, body) {
        res.statusCode.should.equal(405);
        done();
      });
    });
  });

  describe('PUT Method - Create and/or Update', function () {
    it('PUT /library/catalog/book/bk102/author w/o params should return 400', function (done) {
      r.put('http://localhost:6789/library/catalog/book/bk102/author', function (err, res, body) {
        res.statusCode.should.equal(400);
        done();
      });
    });

    it('PUT http://localhost:6789/library/catalog/book/bk102/author?Awesome should update node value', function (done) {
      r.put('http://localhost:6789/library/catalog/book/bk102/author?Awesome', function (err, res, body) {
        res.statusCode.should.equal(202);
        done();
      });
    });

    it('GET /library/catalog/book/bk102/author should return \'Awesome\'', function (done) {
      r.get('http://localhost:6789/library/catalog/book/bk102/author', function (err, res, body) {
        res.statusCode.should.equal(200);
        body.should.equal('Awesome');
        done();
      });
    });
  });

  describe('DELETE Method - Deletion', function () {
    it('DELETE /library/catalog/book/bk102/author should delete with 200', function (done) {
      var option = {
        method: 'delete',
        url: 'http://localhost:6789/library/catalog/book/bk102/author'
      };

      r(option, function (err, res, body) {
        res.statusCode.should.equal(200);
        done();
      });
    });

    it('DELETE /library/catalog/book/bk102/author should not be able to delete again', function (done) {
      var option = {
        method: 'delete',
        url: 'http://localhost:6789/library/catalog/book/bk102/author'
      };

      r(option, function (err, res, body) {
        res.statusCode.should.equal(404);
        done();
      });
    });

    it('GET /library/catalog/book/bk102/author should return 404 to confirm deletion', function (done) {
      r('http://localhost:6789/library/catalog/book/bk102/author', function (err, res, body) {
        res.statusCode.should.equal(404);
        done();
      });
    });

    it('DELETE /customers/Database/Customers/Customer/ should delete with 200', function (done) {
      var option = {
        method: 'delete',
        url: 'http://localhost:6789/customers/Database/Customers'
      };

      r(option, function (err, res, body) {
        res.statusCode.should.equal(200);
        done();
      });
    });

    it('GET /customers/Database/Customers/Customer/GREAL should return 404 to confirm deletion of parent', function (done) {
      r('http://localhost:6789/customers/Database/Customers/Customer/GREAL', function (err, res, body) {
        res.statusCode.should.equal(404);
        done();
      });
    });

    it('GET /customers/Database/Customers/Customer/LAZYK should return 404 to confirm deletion of parent', function (done) {
      r('http://localhost:6789/customers/Database/Customers/Customer/LAZYK', function (err, res, body) {
        res.statusCode.should.equal(404);
        done();
      });
    });
  });

});