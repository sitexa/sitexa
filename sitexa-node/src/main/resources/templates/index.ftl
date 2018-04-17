<#-- @ftlvariable name="top" type="java.util.List<com.sitexa.ktor.model.Sweet>" -->
<#-- @ftlvariable name="latest" type="java.util.List<com.sitexa.ktor.model.Sweet>" -->
<#import "template.ftl" as layout />

<@layout.mainLayout title="Welcome">
<div class="posts">
    <h3 class="content-subhead"><a href="/sweet-top/10/1">Top Sweets</a></h3>
    <@layout.sweets_list sweets=top></@layout.sweets_list>

    <h3 class="content-subhead"><a href="/sweet-latest/10/1">Recent Sweets</a></h3>
    <@layout.sweets_list sweets=latest></@layout.sweets_list>
</div>
</@layout.mainLayout>
