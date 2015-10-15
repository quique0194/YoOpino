import sqlite3
import json
from flask import Flask, request, session, g, redirect, url_for, \
     abort, render_template, flash
from contextlib import closing
import mock_data


DEBUG = True

app = Flask(__name__)


@app.route("/enterprises/")
def get_enterprises():
    print "#################"
    print request.args.get('lat')
    print request.args.get('lon')
    print request.args.get('radius')
    return json.dumps(mock_data.enterprises)

@app.route("/complains/")
def get_complains():
    id = request.args.get('id')
    print "ID", id
    ret = []
    for complain in mock_data.complains:
        if complain["enterprise_id"] == int(id):
            ret.append(complain)
    return json.dumps(ret)

@app.route("/submit_complain/", methods=["GET"])
def submit_complain():
    complain = dict(
        title = request.args.get("title", ""),
        detail = request.args.get("detail", ""),
        enterprise_id = int(request.args.get("enterprise_id"))
    )
    print "COMPLAIN", complain
    mock_data.complains.append(complain)
    return "OK"

if __name__ == "__main__":
    app.run()
