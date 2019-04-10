const app = require("express")();
const values = require("lodash").values;

app.get("/n2o/*", (req, res) => {
  const params = values(req.params).join("/");
  if (params === "page/") {
    const mainPage = require(`./json/main.json`);
    return res.json(mainPage);
  }
  try {
    const json = require(`./json/${params}.json`);
    return res.json(json);
  } catch (e) {
    return res.send(404);
  }
});

app.listen(9090, () => {
  console.log("Example app listening on port 9090!");
});