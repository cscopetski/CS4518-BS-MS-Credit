import express from "express";
import bodyParser from "body-parser";
import session from "express-session";
import cookieParser from "cookie-parser";
import { readFileSync } from "fs";

const app = express();
const users = JSON.parse(readFileSync("users.json", "utf8"));

console.log("List of users:");
console.log(users);

console.log(
  "\n*Normally passwords would never be stored as plain-text, they are here for easy testing purposes*\n"
);

console.log(
  "You can edit the users.json file to add more users\n(make sure new users have a name, username, and password)"
);

app.use(bodyParser.json());
app.use(
  bodyParser.urlencoded({
    extended: true,
  })
);

app.use(cookieParser("v6h23871rvh78123r801t71trv7"));
app.use(
  session({
    secret: "v6h23871rvh78123r801t71trv7",
    resave: false,
    saveUninitialized: false,
    cookie: {
      httpOnly: false,
      maxAge: 1000 * 30 * 24 * 60 * 60,
    },
  })
);

const logger = (req, res, next) => {
  console.log("url:", req.url);
  next();
};

app.use(logger);

app.post("/login", async (req, res) => {
  loginUser(req.body.username, req.body.password)
    .then((data, err) => {
      req.session.name = data.name;
      res.status(200);
      res.json(data);
    })
    .catch((err) => {
      console.log(err);
      res.sendStatus(401);
    });
});

app.post("/logout", async (req, res) => {
  req.session.destroy((err) => {
    if (err) {
      res.sendStatus(400);
    } else {
      res.sendStatus(200);
    }
  });
});

app.get("/name", async (req, res) => {
  res.json(req.session.name);
});

async function loginUser(username, password) {
  let foundUser = undefined;

  await users.some((user) => {
    if (user.username === username) {
      console.log("Found User");
      if (user.password === password) {
        foundUser = { name: user.name, username: user.username };
        return;
      } else {
        console.log("Incorrect Password");
        return;
      }
    }
  });

  if (foundUser !== undefined) {
    return foundUser;
  } else {
    throw new Error("Incorrect username or password");
  }
}

app.listen(process.env.PORT || 3000);
