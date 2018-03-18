<#-- @ftlvariable name="pageUser" type="com.sitexa.ktor.model.User" -->
<#-- @ftlvariable name="sweets" type="java.util.List<com.sitexa.ktor.model.Sweet>" -->

<#import "template.ftl" as layout />

<@layout.mainLayout title="User ${pageUser.displayName}">
<h3>User's sweets</h3>

<@layout.sweets_list sweets=sweets></@layout.sweets_list>
</@layout.mainLayout>
