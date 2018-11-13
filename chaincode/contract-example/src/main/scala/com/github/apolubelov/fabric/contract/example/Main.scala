package com.github.apolubelov.fabric.contract.example

import com.github.apolubelov.fabric.contract._
import com.github.apolubelov.fabric.contract.annotations.{ContractInit, ContractOperation}
import org.hyperledger.fabric.shim.ChaincodeBase
import org.slf4j.{Logger, LoggerFactory}

/*
 * This is just a version of
 * https://github.com/hyperledger/fabric-samples/blob/release-1.3/chaincode/chaincode_example02/java/src/main/java/org/hyperledger/fabric/example/SimpleChaincode.java
 * Hyperledger Fabric example, ported to Scala with use of fabric-contract-base library.
 *
 * @author Alexey Polubelov
 */
object Main extends ContractBase with App {

    // start SHIM chain code
    start(args)

    // setup logging levels
    LoggerFactory
      .getLogger(Logger.ROOT_LOGGER_NAME)
      .asInstanceOf[ch.qos.logback.classic.Logger]
      .setLevel(ch.qos.logback.classic.Level.INFO)

    LoggerFactory
      .getLogger(classOf[ContractBase].getPackage.getName)
      .asInstanceOf[ch.qos.logback.classic.Logger]
      .setLevel(ch.qos.logback.classic.Level.TRACE)


    @ContractInit
    def init(context: ContractContext, ac1: String, ac1Value: Int, ac2: String, ac2Value: Int): Unit = {
        context.store.put(ac1, ac1Value)
        context.store.put(ac2, ac2Value)
        // bonus content:
        putAsset(context, "a1", DummyAsset("a1", 1, 1.1))
        putAsset(context, "a2", DummyAsset("a2", 2, 2.2))
        putAsset(context, "a3", DummyAsset("a3", 3, 3.3))
    }

    @ContractOperation
    def invoke(context: ContractContext, from: String, to: String, x: Int): ContractResponse =
        context.store.get[Int](from).map { source =>
            context.store.get[Int](to).map { dest =>
                context.store.put(from, source - x)
                context.store.put(to, dest + x)
                Success()
            } getOrElse Error(s"No value of $to in the Ledger")
        } getOrElse Error(s"No value of $from in the Ledger")


    @ContractOperation
    def delete(context: ContractContext, key: String): Unit = {
        context.store.del[Int](key)
    }

    @ContractOperation
    def query(context: ContractContext, key: String): ContractResponse =
        context.store.get[Int](key).map { value =>
            Success(value)
        } getOrElse Error(s"""{"Error":"Nil amount for "$key"}""")

    //
    // Bonus content
    //

    case class DummyAsset(
        name: String,
        aType: Int,
        value: Double
    )

    override def resolveClassByName(name: String): Option[Class[_]] = Some(classOf[DummyAsset])

    @ContractOperation
    def putAsset(context: ContractContext, address: String, asset: DummyAsset): ContractResponse = {
        context.store.put(address, asset)
        Success()
    }

    @ContractOperation
    def getAsset(context: ContractContext, address: String): ContractResponse = {
        val asset: Option[DummyAsset] = context.store.get[DummyAsset](address)
        asset.map { asset => Success(asset) } getOrElse Error(s"No Asset with address $address")
    }

    @ContractOperation
    def listAssets(context: ContractContext): ContractResponse = {
        val assets: Array[DummyAsset] = context.store.list[DummyAsset]
          .map(_._2) // take only values
          .toArray // use Array, as GSON knows nothing about scala collections
        Success(assets)
    }

}
