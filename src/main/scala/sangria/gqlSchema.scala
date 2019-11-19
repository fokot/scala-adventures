//import cats.syntax.option._
//import com.upstartcommerce.tokenauthsupport.{Roles, Token, TokenType, auth}
//import sangria.execution.FieldTag
//import sangria.schema.{Argument, Field, ObjectType, Schema, StringType, fields}
//
//case class GQLContext(token: Option[Token])
//
//case object Authorized extends FieldTag
//
//object gqlSchema {
//
//  val QueryType = ObjectType[GQLContext, Unit](
//    "Query",
//    fields[GQLContext, Unit](
//      Field(
//        "product",
//        StringType,
//        description = Some("Returns a product with specific `id`."),
//        arguments = Nil,
//        resolve = _ => "aaa",
//        tags = List(Authorized)
//      )
//    )
//  )
//
//  val LoginType = ObjectType[GQLContext, Token](
//    "Login",
//    fields[GQLContext, Token](Field("token", StringType, resolve = ctx => auth.encodeToken(ctx.value)))
//  )
//
//  val MutationType = ObjectType[GQLContext, Unit](
//    "Mutation",
//    fields[GQLContext, Unit](
//      Field(
//        "login",
//        LoginType,
//        description = Some("Returns a product with specific `id`."),
//        arguments = Argument("login", StringType) :: Argument("password", StringType) :: Nil,
//        resolve = _ => Token("authorId", "login", Map("lessee" -> Roles.userRoles), false, TokenType.Access)
//      )
//    )
//  )
//
//  val schema = Schema(QueryType, MutationType.some)
//}
