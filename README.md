# fabric-contract-examples

Example smart contract on [Scala](https://www.scala-lang.org/) demonstrating use of [fabric-contract-base](https://github.com/apolubelov/fabric-contract-base) library with demo fabric network to run it locally.
The example code could be found at **chaincode/contract-example**.

First time run: pull scalaenv image, generate network artifacts:
```bash
docker pull apolubelov/fabric-scalaenv
./byfn.sh generate
```
Run the network (includes simple test execution)
```bash
./byfn.sh up
```
Stop the network:
```bash
./byfn.sh down
```

**ACKNOWLEDGE**: The demo network stuff is a bit stripped and adjusted copy of [Build Your First Network](https://github.com/hyperledger/fabric-samples/tree/release-1.3/first-network) sample.
