import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class WeatherAnalyzer {

	public static void main(String[] args) {
		List<WeatherDetailsDto> listOfWeatherReportDetailsDto = readWeatherReportsFile();
		List<WeatherDetailsDto> listOfMaximumTemperatureLocations = getMaximumTemperatureLocations(
				listOfWeatherReportDetailsDto);
		List<WeatherDetailsDto> listOfHighestWindSpeedLocations = getHighestWindSpeedLocations(
				listOfWeatherReportDetailsDto);
		Map<String, Float> listOfTopTenNicestPlacesToLive = getTopTenNicestPlacesToLive(listOfWeatherReportDetailsDto);
		printAllWeatherReports(listOfMaximumTemperatureLocations, listOfHighestWindSpeedLocations,
				listOfTopTenNicestPlacesToLive);
	}

	private static void printAllWeatherReports(List<WeatherDetailsDto> listOfMaximumTemperatureLocations,
			List<WeatherDetailsDto> listOfHighestWindSpeedLocations, Map<String, Float> mapOfTopTenNicestPlacesToLive) {

		try {
			FileWriter fw = new FileWriter("sample-output.txt");
			fw.write(
					"-------------------------------------------------------------------------------------------------------\n");
			fw.write("PART 1: Find where and when it was the hottest in the US in 2016 \n");
			for (WeatherDetailsDto maximumTemperatureDto : listOfMaximumTemperatureLocations) {
				fw.write("\n(" + maximumTemperatureDto.getMaximumTemperature() + ",'"
						+ maximumTemperatureDto.getStationLocation() + "'," + maximumTemperatureDto.getFullDate()
						+ ")");
			}
			fw.write(
					"\n-------------------------------------------------------------------------------------------------------\n");
			fw.write("PART 2: Find the highest wind speed recorded in each state\n");
			for (WeatherDetailsDto highestWindSpeedDto : listOfHighestWindSpeedLocations) {
				fw.write("\n('" + highestWindSpeedDto.getStationLocation() + "'," + highestWindSpeedDto.getWindSpeed()
						+ ")");

			}
			fw.write(
					"\n-------------------------------------------------------------------------------------------------------\n");
			fw.write("PART 3: Find the top 10 nicest places to live\n");

			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			for (Entry<String, Float> entry : mapOfTopTenNicestPlacesToLive.entrySet()) {
				fw.write("\n('" + entry.getKey() + "'," + df.format(entry.getValue()) + "%)");

			}
			fw.write(
					"\n-------------------------------------------------------------------------------------------------------");

			fw.close();
		} catch (IOException e) {
			System.out.println("Exception In printAllWeatherReports: " + e.getMessage());

		}
	}

	private static Map<String, Float> getTopTenNicestPlacesToLive(
			List<WeatherDetailsDto> listOfWeatherReportDetailsDto) {
		Map<String, List<WeatherDetailsDto>> mapOfTopNicestPlacesToLive = new HashMap<>();
		Map<String, Float> listOfTopTanNicestPlacesToLive = new HashMap<>();
		try {
			Map<String, List<WeatherDetailsDto>> mapOfCitesDetails = getDistinctCitiesWithData(
					listOfWeatherReportDetailsDto);
			for (Map.Entry<String, List<WeatherDetailsDto>> entry : mapOfCitesDetails.entrySet()) {
				List<WeatherDetailsDto> listOfNicestWeatherDetailsDto = new ArrayList<>();
				for (WeatherDetailsDto weatherDetailsDto : entry.getValue()) {
					if ((weatherDetailsDto.getMaximumTemperature() <= 40
							&& weatherDetailsDto.getMinimumTemperature() >= 20)
							&& (weatherDetailsDto.getWindSpeed() >= 10 && weatherDetailsDto.getWindSpeed() <= 40)) {
						listOfNicestWeatherDetailsDto.add(weatherDetailsDto);
					}
				}
				if (!listOfNicestWeatherDetailsDto.isEmpty()) {
					mapOfTopNicestPlacesToLive.put(entry.getKey(), listOfNicestWeatherDetailsDto);
				}
			}

			return listOfTopTanNicestPlacesToLive = getFinalTopTanNicestPlacesToLive(mapOfTopNicestPlacesToLive,
					mapOfCitesDetails);
		} catch (Exception e) {
			System.out.println("Exception In getTopTanNicestPlacesToLive:" + e.getMessage());
		}
		return null;
	}

	private static Map<String, Float> getFinalTopTanNicestPlacesToLive(
			Map<String, List<WeatherDetailsDto>> mapOfTopNicestPlacesToLive,
			Map<String, List<WeatherDetailsDto>> mapOfCitesDetails) {
		try {
			Map<String, Float> MapOfCityPercentage = new HashMap<>();
			for (Map.Entry<String, List<WeatherDetailsDto>> topNicestPlacesEntry : mapOfTopNicestPlacesToLive
					.entrySet()) {
				String cityKey = topNicestPlacesEntry.getKey();
				float percentage = (topNicestPlacesEntry.getValue().size() * 100f)
						/ mapOfCitesDetails.get(cityKey).size();
				MapOfCityPercentage.put(cityKey, percentage);

			}
			return sortByValue(MapOfCityPercentage);
		} catch (Exception e) {
			System.out.println("Exception In getFinalTopTanNicestPlacesToLive:" + e.getMessage());
		}

		return null;
	}

	public static Map<String, Float> sortByValue(Map<String, Float> dataMap) {
		HashMap<String, Float> mapOfTopTanCites = new LinkedHashMap<>();
		try {
			List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(dataMap.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
				public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
					return (o2.getValue()).compareTo(o1.getValue());
				}
			});
			HashMap<String, Float> temp = new LinkedHashMap<String, Float>();
			for (Map.Entry<String, Float> mValue : list) {
				temp.put(mValue.getKey(), mValue.getValue());
			}
			if (temp.size() > 10) {
				for (int i = 0; i < 10; i++) {
					Object firstKey = temp.keySet().toArray()[i];

					mapOfTopTanCites.put(firstKey.toString(), temp.get(firstKey));

				}
			} else {
				for (int i = 0; i < temp.size(); i++) {
					Object firstKey = temp.keySet().toArray()[i];

					mapOfTopTanCites.put(firstKey.toString(), temp.get(firstKey));

				}
			}
		} catch (Exception e) {
			System.out.println("Exception In sortByValue:" + e.getMessage());
		}

		return mapOfTopTanCites;
	}

	private static Map<String, List<WeatherDetailsDto>> getDistinctCitiesWithData(
			List<WeatherDetailsDto> listOfWeatherReportDetailsDto) {
		try {

			Map<String, List<WeatherDetailsDto>> mapOfCitesWiseShortDetails = new HashMap<>();

			for (WeatherDetailsDto weatherDetailsDto : listOfWeatherReportDetailsDto) {
				if (!mapOfCitesWiseShortDetails.containsKey(weatherDetailsDto.getStationCity())) {
					List<WeatherDetailsDto> weatherDetailsDtoList = new ArrayList<>();
					weatherDetailsDtoList.add(weatherDetailsDto);
					mapOfCitesWiseShortDetails.put(weatherDetailsDto.getStationCity(), weatherDetailsDtoList);

				} else {
					mapOfCitesWiseShortDetails.get(weatherDetailsDto.getStationCity()).add(weatherDetailsDto);
				}
			}
			return mapOfCitesWiseShortDetails;
		} catch (Exception e) {
			System.out.println("Exception In getDistinctCitiesWithData:" + e.getMessage());
		}
		return null;
	}

	private static List<WeatherDetailsDto> getHighestWindSpeedLocations(
			List<WeatherDetailsDto> listOfWeatherReportDetailsDto) {
		try {
			List<WeatherDetailsDto> listOfHighestWindSpeed = new ArrayList<>();
			WeatherDetailsDto HighestWindSpeedDto = Collections.max(listOfWeatherReportDetailsDto,
					Comparator.comparing(s -> s.getWindSpeed()));
			for (WeatherDetailsDto watherDetailsDto : listOfWeatherReportDetailsDto) {
				if (watherDetailsDto.getWindSpeed() == HighestWindSpeedDto.getWindSpeed()) {
					listOfHighestWindSpeed.add(watherDetailsDto);
				}
			}
			return listOfHighestWindSpeed;
		} catch (Exception e) {
			System.out.println("Exception In getHighestWindSpeedLocations:" + e.getMessage());
		}
		return null;
	}

	private static List<WeatherDetailsDto> getMaximumTemperatureLocations(
			List<WeatherDetailsDto> listOfWeatherReportDetailsDto) {
		try {
			List<WeatherDetailsDto> listMaximumTemperatureLocations = new ArrayList<>();
			WeatherDetailsDto maxTempDto = Collections.max(listOfWeatherReportDetailsDto,
					Comparator.comparing(s -> s.getMaximumTemperature()));
			for (WeatherDetailsDto watherDetailsDto : listOfWeatherReportDetailsDto) {
				if (watherDetailsDto.getMaximumTemperature() == maxTempDto.getMaximumTemperature()) {
					listMaximumTemperatureLocations.add(watherDetailsDto);
				}
			}
			return listMaximumTemperatureLocations;
		} catch (Exception e) {
			System.out.println("Exception In getMaximumTemperatureLocations:" + e.getMessage());
		}
		return null;
	}

	public static List<WeatherDetailsDto> readWeatherReportsFile() {
		try {

			// String filePath = System.getProperty("user.home") + File.separator +
			// "Desktop" + File.separator + "weather" + File.separator + "weather.csv";
			String filePath = "weather-data-input.csv";
			File file = new File(filePath);
			if (file.exists()) {
				List<WeatherDetailsDto> ListOfWeatherReportDetailsDto = new ArrayList<WeatherDetailsDto>();
				BufferedReader br = new BufferedReader(new FileReader(filePath));
				String line = "";
				boolean isHeader = false;
				while ((line = br.readLine()) != null) {
					WeatherDetailsDto weatherDetailsDto = new WeatherDetailsDto();
					if (isHeader && !line.isEmpty()) {
						String data[] = line.split(",");
						weatherDetailsDto.setDataPrecipitation(Float.parseFloat(data[0].replaceAll("\"", "").trim()));
						weatherDetailsDto.setFullDate(data[1].replaceAll("\"", "").trim());
						weatherDetailsDto.setMonth(Integer.parseInt(data[2].replaceAll("\"", "").trim()));
						weatherDetailsDto.setWeek(Integer.parseInt(data[3].replaceAll("\"", "").trim()));
						weatherDetailsDto.setYear(Integer.parseInt(data[4].replaceAll("\"", "").trim()));
						weatherDetailsDto.setStationCity(data[5].replaceAll("\"", "").trim());
						weatherDetailsDto.setStationCode(data[6].replaceAll("\"", "").trim());
						weatherDetailsDto.setStationLocation(
								data[7].replaceAll("\"", "").trim() + "," + data[8].replaceAll("\"", "").trim());
						weatherDetailsDto.setStationState(data[9].replaceAll("\"", "").trim());
						weatherDetailsDto.setAverageTemperature(Integer.parseInt(data[10].replaceAll("\"", "").trim()));
						weatherDetailsDto.setMaximumTemperature(Integer.parseInt(data[11].replaceAll("\"", "").trim()));
						weatherDetailsDto.setMinimumTemperature(Integer.parseInt(data[12].replaceAll("\"", "").trim()));
						weatherDetailsDto.setWindDirection(Integer.parseInt(data[13].replaceAll("\"", "").trim()));
						weatherDetailsDto.setWindSpeed(Float.parseFloat(data[14].replaceAll("\"", "").trim()));
						ListOfWeatherReportDetailsDto.add(weatherDetailsDto);

					}
					isHeader = true;
				}

				br.close();
				return ListOfWeatherReportDetailsDto;

			} else {
				System.out.println("File Not Found.");
			}
		} catch (Exception e) {
			System.err.println("Exception in readDictionaryFile : " + e.getMessage());
		}
		return null;
	}

}
