/**
 * Copyright (c) 2015-2021 TomTom N.V. All rights reserved.
 * <p>
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used
 * for internal evaluation purposes or commercial use strictly subject to separate licensee
 * agreement between you and TomTom. If you are the licensee, you are only permitted to use
 * this Software in accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */
package com.fxc.ev.launcher.maps;

import com.fxc.ev.launcher.utils.Locations;
import com.fxc.ev.launcher.maps.routeconfig.RouteConfigExample;
import com.tomtom.online.sdk.common.location.BoundingBox;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.routing.ev.EvRouteSpecification;
import com.tomtom.online.sdk.routing.ev.charging.ChargingConnection;
import com.tomtom.online.sdk.routing.ev.charging.ChargingCurveSupportPoint;
import com.tomtom.online.sdk.routing.ev.charging.ChargingDescriptor;
import com.tomtom.online.sdk.routing.ev.charging.ChargingMode;
import com.tomtom.online.sdk.routing.ev.charging.FacilityType;
import com.tomtom.online.sdk.routing.ev.charging.PlugType;
import com.tomtom.online.sdk.routing.route.CombustionVehicleDescriptor;
import com.tomtom.online.sdk.routing.route.ElectricVehicleDescriptor;
import com.tomtom.online.sdk.routing.route.RouteCalculationDescriptor;
import com.tomtom.online.sdk.routing.route.RouteDescriptor;
import com.tomtom.online.sdk.routing.route.RouteSpecification;
import com.tomtom.online.sdk.routing.route.calculation.InstructionsType;
import com.tomtom.online.sdk.routing.route.calculation.SectionType;
import com.tomtom.online.sdk.routing.route.description.AvoidType;
import com.tomtom.online.sdk.routing.route.description.RouteType;
import com.tomtom.online.sdk.routing.route.description.TravelMode;
import com.tomtom.online.sdk.routing.route.diagnostic.ReportType;
import com.tomtom.online.sdk.routing.route.vehicle.CombustionVehicleConsumption;
import com.tomtom.online.sdk.routing.route.vehicle.ElectricVehicleConsumption;
import com.tomtom.online.sdk.routing.route.vehicle.VehicleDimensions;
import com.tomtom.online.sdk.routing.route.vehicle.VehicleEfficiency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteSpecificationFactory {

    private static final double MAX_CHARGE_IN_KWH = 85.0;
    private static final double CURRENT_CHARGE_IN_KWH = 43.0;
    private static final double AUXILIARY_POWER_IN_KW = 1.7;
    private static final double CURRENT_FUEL_IN_LITERS = 50.0;
    private static final double AUXILIARY_POWER_IN_LITERS_PER_HOUR = 0.2;
    private static final double FUEL_ENERGY_DENSITY_IN_MJOULES_PER_LITER = 34.2;

    public static RouteSpecification createAvoidRouteSpecification(AvoidType avoidType, RouteConfigExample routeConfig) {
        List<AvoidType> avoidTypes = new ArrayList<>();
        avoidTypes.add(avoidType);
        //tag::doc_route_avoids[]
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .avoidType(avoidTypes)
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(0)
                .reportType(ReportType.NONE)
                .instructionType(InstructionsType.NONE)
                .build();

        RouteSpecification routeSpecification = new RouteSpecification.Builder(
                routeConfig.getOrigin(),
                routeConfig.getDestination()
        )
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
        //end::doc_route_avoids[]
        return routeSpecification;
    }

    public static RouteSpecification createRouteConsumptionElectricSpecification(RouteConfigExample routeConfig) {
        //tag::doc_consumption_model_electric[]
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(2)
                .build();

        //vehicle specific consumption model <speed, consumption in kWh>
        Map<Double, Double> speedToConsumptionMap = new HashMap<>();
        speedToConsumptionMap.put(10.0, 5.0);
        speedToConsumptionMap.put(30.0, 10.0);
        speedToConsumptionMap.put(50.0, 15.0);
        speedToConsumptionMap.put(70.0, 20.0);
        speedToConsumptionMap.put(90.0, 25.0);
        speedToConsumptionMap.put(120.0, 30.0);

        ElectricVehicleConsumption electricVehicleConsumption = new ElectricVehicleConsumption(
                MAX_CHARGE_IN_KWH,
                CURRENT_CHARGE_IN_KWH,
                AUXILIARY_POWER_IN_KW,
                speedToConsumptionMap);

        ElectricVehicleDescriptor electricVehicleDescriptor = new ElectricVehicleDescriptor.Builder(electricVehicleConsumption)
                .vehicleDimensions(new VehicleDimensions.Builder()
                        .vehicleWeightInKg(1600)
                        .build()
                )
                .vehicleEfficiency(new VehicleEfficiency.Builder()
                        .accelerationEfficiency(0.33)
                        .decelerationEfficiency(0.33)
                        .downhillEfficiency(0.33)
                        .uphillEfficiency(0.33)
                        .build()
                )
                .build();

        RouteSpecification routeSpecification = new RouteSpecification.Builder(routeConfig.getOrigin(), routeConfig.getDestination())
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .electricVehicleDescriptor(electricVehicleDescriptor)
                .build();
        //end::doc_consumption_model_electric[]
        return routeSpecification;
    }

    public static RouteSpecification createRouteConsumptionCombustionSpecification(RouteConfigExample routeConfig) {
        //tag::doc_consumption_model_combustion[]
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(2)
                .build();

        //vehicle specific consumption model <speed, consumption in liters>
        Map<Double, Double> speedToConsumptionMap = new HashMap<>();
        speedToConsumptionMap.put(10.0, 6.5);
        speedToConsumptionMap.put(30.0, 7.0);
        speedToConsumptionMap.put(50.0, 8.0);
        speedToConsumptionMap.put(70.0, 8.4);
        speedToConsumptionMap.put(90.0, 7.7);
        speedToConsumptionMap.put(120.0, 7.5);
        speedToConsumptionMap.put(150.0, 9.0);

        CombustionVehicleConsumption combustionVehicleConsumption = new CombustionVehicleConsumption(
                CURRENT_FUEL_IN_LITERS,
                AUXILIARY_POWER_IN_LITERS_PER_HOUR,
                FUEL_ENERGY_DENSITY_IN_MJOULES_PER_LITER,
                speedToConsumptionMap
        );

        CombustionVehicleDescriptor combustionVehicleDescriptor = new CombustionVehicleDescriptor.Builder()
                .vehicleConsumption(combustionVehicleConsumption)
                .vehicleDimensions(new VehicleDimensions.Builder()
                        .vehicleWeightInKg(1600)
                        .build()
                )
                .vehicleEfficiency(new VehicleEfficiency.Builder()
                        .accelerationEfficiency(0.33)
                        .decelerationEfficiency(0.33)
                        .downhillEfficiency(0.33)
                        .uphillEfficiency(0.33)
                        .build()
                )
                .build();

        RouteSpecification routeSpecification = new RouteSpecification.Builder(routeConfig.getOrigin(), routeConfig.getDestination())
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .combustionVehicleDescriptor(combustionVehicleDescriptor)
                .build();
        //end::doc_consumption_model_combustion[]
        return routeSpecification;
    }

    public static RouteSpecification createRouteManeuversSpecification(String language, RouteConfigExample routeConfig) {
        //tag::doc_route_language[]
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(0)
                .reportType(ReportType.NONE)
                .instructionType(InstructionsType.TEXT)
                .language(language)
                .build();

        RouteSpecification routeSpecification = new RouteSpecification.Builder(
                routeConfig.getOrigin(),
                routeConfig.getDestination()
        )
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
        //end::doc_route_language[]
        return routeSpecification;
    }

    public static RouteSpecification createRouteTravelModesSpecification(TravelMode travelMode, RouteConfigExample routeConfig) {
        //tag::doc_route_travel_mode[]
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .travelMode(travelMode)
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(0)
                .reportType(ReportType.NONE)
                .instructionType(InstructionsType.NONE)
                .build();

        RouteSpecification routeSpecification = new RouteSpecification.Builder(
                routeConfig.getOrigin(),
                routeConfig.getDestination()
        )
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
        //end::doc_route_travel_mode[]
        return routeSpecification;
    }

    public static RouteSpecification createRouteTypesSpecification(RouteType routeType, RouteConfigExample routeConfig) {
        //tag::doc_route_type[]
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .routeType(routeType)
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(0)
                .reportType(ReportType.EFFECTIVE_SETTINGS)
                .instructionType(InstructionsType.TEXT)
                .build();

        RouteSpecification routeSpecification = new RouteSpecification.Builder(
                routeConfig.getOrigin(),
                routeConfig.getDestination()
        )
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
        //end::doc_route_type[]
        return routeSpecification;
    }

    public static RouteSpecification createArrivalRouteSpecification(Date arrivalTime, RouteConfigExample routeConfig) {
        //tag::doc_route_arrival_time[]
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .arriveAt(arrivalTime)
                .reportType(ReportType.EFFECTIVE_SETTINGS)
                .instructionType(InstructionsType.TEXT)
                .build();

        RouteSpecification routeSpecification = new RouteSpecification.Builder(
                routeConfig.getOrigin(),
                routeConfig.getDestination()
        )
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
        //end::doc_route_arrival_time[]
        return routeSpecification;
    }

    public static RouteSpecification createDepartureRouteSpecification(Date departureTime, RouteConfigExample routeConfig) {
        //tag::doc_route_departure_time[]
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .departAt(departureTime)
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .reportType(ReportType.EFFECTIVE_SETTINGS)
                .instructionType(InstructionsType.TEXT)
                .build();

        return new RouteSpecification.Builder(routeConfig.getOrigin(), routeConfig.getDestination())
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
        //end::doc_route_departure_time[]
    }

    public static RouteSpecification createRouteAlternativesSpecification(int maxAlternatives, RouteConfigExample routeConfig) {
        //tag::doc_route_alternatives[]
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(maxAlternatives)
                .reportType(ReportType.EFFECTIVE_SETTINGS)
                .instructionType(InstructionsType.TEXT)
                .build();

        RouteSpecification routeSpecification = new RouteSpecification.Builder(
                routeConfig.getOrigin(),
                routeConfig.getDestination()
        )
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
        //end::doc_route_alternatives[]
        return routeSpecification;
    }

    public static RouteSpecification createRouteForAlongRouteSearch(RouteConfigExample routeConfig) {
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .routeType(RouteType.FASTEST)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(0)
                .reportType(ReportType.EFFECTIVE_SETTINGS)
                .instructionType(InstructionsType.TEXT)
                .sectionType(SectionType.TRAFFIC)
                .build();

        return new RouteSpecification.Builder(routeConfig.getOrigin(), routeConfig.getDestination())
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
    }

    //metis@210018 add
    public static RouteSpecification createRouteForAlongRouteSearch(LatLng origin, LatLng destination) {
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(true)
                .routeType(RouteType.FASTEST)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(0)
                .reportType(ReportType.EFFECTIVE_SETTINGS)
                .instructionType(InstructionsType.TEXT)
                .sectionType(SectionType.TRAFFIC)
                .build();

        return new RouteSpecification.Builder(origin, destination)
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
    }

    public static RouteSpecification createBaseRouteForAvoidsVignettesAndAreas(RouteConfigExample routeConfig) {
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(0)
                .reportType(ReportType.NONE)
                .instructionType(InstructionsType.NONE)
                .build();

        return new RouteSpecification.Builder(routeConfig.getOrigin(), routeConfig.getDestination())
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
    }

    public static RouteSpecification createRouteForAvoidsVignettesAndAreas(List<String> avoidVignettesList, RouteConfigExample routeConfig) {
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(0)
                .reportType(ReportType.NONE)
                .instructionType(InstructionsType.NONE)
                .avoidVignettes(avoidVignettesList)
                .build();

        return new RouteSpecification.Builder(routeConfig.getOrigin(), routeConfig.getDestination())
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
    }

    public static RouteSpecification createRouteForAvoidsArea(List<BoundingBox> avoidAreas, RouteConfigExample routeConfig) {
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .maxAlternatives(0)
                .reportType(ReportType.NONE)
                .instructionType(InstructionsType.NONE)
                .avoidAreas(avoidAreas)
                .build();

        return new RouteSpecification.Builder(routeConfig.getOrigin(), routeConfig.getDestination())
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
    }

    @SuppressWarnings("unused")
    public static EvRouteSpecification createEvRouteSpecification() {
        double auxiliaryPowerInKw = 1.0;
        double minChargeAtDestinationInKwh = 4.0;
        double minChargeAtChargingStopsInKwh = 8.0;
        Map<Double, Double> speedConsumptionInKwhPer100Km = new HashMap<>();

        //tag::doc_ev_short_range[]
        ElectricVehicleDescriptor evDescriptor = new ElectricVehicleDescriptor.Builder(
                new ElectricVehicleConsumption(
                        40.0,
                        20.0,
                        auxiliaryPowerInKw,
                        speedConsumptionInKwhPer100Km
                )
        ).build();

        ChargingDescriptor chargingDescriptor = new ChargingDescriptor(
                minChargeAtDestinationInKwh,
                minChargeAtChargingStopsInKwh,
                Collections.singletonList(
                        new ChargingMode(
                                Collections.singletonList(
                                        new ChargingConnection(
                                                FacilityType.CHARGE_380_TO_480V_3_PHASE_AT_32A,
                                                PlugType.IEC_62196_TYPE_2_OUTLET
                                        )
                                ),
                                Arrays.asList(
                                        new ChargingCurveSupportPoint(6.0, 360),
                                        new ChargingCurveSupportPoint(40.0, 4680)
                                )
                        )
                )
        );
        //end::doc_ev_short_range[]
        //tag::doc_plan_ev_route[]
        EvRouteSpecification evRouteSpecification = new EvRouteSpecification.Builder(
                Locations.AMSTERDAM_LOCATION,
                Locations.EINDHOVEN_LOCATION,
                evDescriptor,
                chargingDescriptor
        ).build();
        //end::doc_plan_ev_route[]
        return evRouteSpecification;
    }

    @SuppressWarnings("unused")
    private void createLongRangeEvVehicle() {
        double auxiliaryPowerInKw = 1.0;
        double minChargeAtDestinationInKwh = 4.0;
        double minChargeAtChargingStopsInKwh = 8.0;
        Map<Double, Double> speedConsumptionInKwhPer100Km = new HashMap<>();

        //tag::doc_ev_long_range[]
        ElectricVehicleDescriptor evDescriptor = new ElectricVehicleDescriptor.Builder(
                new ElectricVehicleConsumption(
                        80.0,
                        40.0,
                        auxiliaryPowerInKw,
                        speedConsumptionInKwhPer100Km
                )
        ).build();

        ChargingDescriptor chargingDescriptor = new ChargingDescriptor(
                minChargeAtDestinationInKwh,
                minChargeAtChargingStopsInKwh,
                Collections.singletonList(
                        new ChargingMode(
                                Collections.singletonList(
                                        new ChargingConnection(
                                                FacilityType.CHARGE_380_TO_480V_3_PHASE_AT_32A,
                                                PlugType.IEC_62196_TYPE_2_OUTLET
                                        )
                                ),
                                Arrays.asList(
                                        new ChargingCurveSupportPoint(6.0, 360),
                                        new ChargingCurveSupportPoint(80.0, 6680)
                                )
                        )
                )
        );
        //end::doc_ev_long_range[]
    }
}
