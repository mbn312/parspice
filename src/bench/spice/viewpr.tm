KPL/MK

      Example meta-kernel for geometric event finding hands-on
      coding lesson.

         Version 2.0.0 13-JUL-2017 (JDR)

      The names and contents of the kernels referenced by this
      meta-kernel are as follows:

      File Name                       Description
      ------------------------------  ------------------------------
      de405xs.bsp                     Planetary ephemeris SPK,
                                      subsetted to cover only
                                      time range of interest.
      earthstns_itrf93_050714.bsp     DSN station SPK.
      earth_topo_050714.tf            DSN station frame definitions.
      earth_000101_060525_060303.bpc  Binary PCK for Earth.
      naif0008.tls                    Generic LSK.
      ORMM__040501000000_00076XS.BSP  MEX Orbiter trajectory SPK,
                                      subsetted to cover only
                                      time range of interest.
      pck00008.tpc                    Generic PCK.


   \begindata

      KERNELS_TO_LOAD = (

              'src/bench/spice/gfposc-kernels/spk/de405xs.bsp'
              'src/bench/spice/gfposc-kernels/spk/earthstns_itrf93_050714.bsp'
              'src/bench/spice/gfposc-kernels/fk/earth_topo_050714.tf'
              'src/bench/spice/gfposc-kernels/pck/earth_000101_060525_060303.bpc'
              'src/bench/spice/gfposc-kernels/lsk/naif0008.tls'
              'src/bench/spice/gfposc-kernels/spk/ORMM__040501000000_00076XS.BSP'
              'src/bench/spice/gfposc-kernels/pck/pck00008.tpc'
                        )

   \begintext