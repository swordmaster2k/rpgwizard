export default async function (origin) {
    // console.log("sleep thread test origin=[%s]", origin.name);
    await new Promise(resolve => setTimeout(resolve, 5000));
    // console.log("awoke thread test origin=[%s]", origin.name);
}
