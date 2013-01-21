#
# This Makefile is responsoble for build debian package.
# 4 variables must be provided:
#   HSN2_VER=2.X
#   BUILD_NUMBER=999
#   DEBIAN_DIST=(experimental|2.X)

DEBIAN_DIST=experimental
HSN2_COMPONENT=reporter

all:	hsn2-reporter

clean:	hsn2-reporter-clean

PKG=hsn2-$(HSN2_COMPONENT)_$(HSN2_VER)-$(BUILD_NUMBER)_all
hsn2-reporter: hsn2-reporter-clean
	mkdir -p $(PKG)/DEBIAN
	mkdir -p $(PKG)/etc/hsn2/templates
	mkdir -p $(PKG)/etc/init.d
	mkdir -p $(PKG)/opt/hsn2/$(HSN2_COMPONENT)
	mkdir -p $(PKG)/var/log/hsn2
	tar -zxf ../target/hsn2-reporter-*.tar.gz -C $(PKG)/opt/hsn2/$(HSN2_COMPONENT)
	cp reporter.initd $(PKG)/etc/init.d/hsn2-$(HSN2_COMPONENT)
	cp ../../../../docs/Design/jsont/* $(PKG)/etc/hsn2/templates
	cp control conffiles $(PKG)/DEBIAN
	sed -i "s/{VER}/${HSN2_VER}-${BUILD_NUMBER}/" $(PKG)/DEBIAN/control
	sed -i "s/{DEBIAN_DIST}/${DEBIAN_DIST}/" $(PKG)/DEBIAN/control
	fakeroot dpkg -b $(PKG)

hsn2-reporter-clean:
	rm -rf hsn2-$(HSN2_COMPONENT)_*
