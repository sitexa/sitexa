<#-- @ftlvariable name="top" type="java.util.List<com.sitexa.ktor.model.Sweet>" -->
<#-- @ftlvariable name="latest" type="java.util.List<com.sitexa.ktor.model.Sweet>" -->

<#import "template.ftl" as layout />

<@layout.mainLayout title="Welcome">
<div class="posts">
    <h3 class="content-subhead">Recent 10</h3>
    <@layout.sweets_list sweets=latest></@layout.sweets_list>
    <div class="nav-page">
        <a href="/sweet-latest/${count}/${page-1}">last</a> <a href="/sweet-latest/${count}/${page+1}">next</a>
    </div>
</div>
</@layout.mainLayout>
