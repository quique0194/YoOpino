import sqlite3
import json
from flask import Flask, request, session, g, redirect, url_for, \
     abort, render_template, flash
from contextlib import closing
import mock_data

DATABASE = "flaskr.db"
DEBUG = True
SECRET_KEY = 'development key'
USERNAME = 'admin'
PASSWORD = 'admin'

app = Flask(__name__)
app.config.from_object(__name__)

def insert_mock_data():
    db = connect_db()
    for enterprise in mock_data.enterprises:
        db.execute("insert into enterprises (name, category, img) values (?, ?, ?)",
                    [enterprise["name"], enterprise["category"], enterprise["img"]])
    for queja in mock_data.quejas:
        db.execute("insert into quejas (complain, enterprise_id) values (?, ?)",
                    [queja["complain"], queja["enterprise_id"]])
    db.commit()
    db.close()


def init_db():
    with closing(connect_db()) as db:
        with app.open_resource('schema.sql', mode='r') as f:
            db.cursor().executescript(f.read())
        db.commit()

def connect_db():
    return sqlite3.connect(app.config['DATABASE'])


@app.before_request
def before_request():
    g.db = connect_db()

@app.teardown_request
def teardown_request(exception):
    db = getattr(g, 'db', None)
    if db is not None:
        db.close()

@app.route("/enterprises/")
def get_enterprises():
    print "#################"
    print request.args.get('lat')
    print request.args.get('lon')
    print request.args.get('radius')
    cur = g.db.execute('select id, name, category, img from enterprises')
    enterprises = [dict(id=row[0], 
                        name=row[1], 
                        category=row[2], 
                        img=row[3]) for row in cur.fetchall()]
    return json.dumps(enterprises)

if __name__ == "__main__":
    app.run()
